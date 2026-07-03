package dev.mrshawn.pokeblocks.shape;

/**
 * Minimal immutable 3x4 affine transform (row-major, column-vector convention: {@code v' = M·v}),
 * used by the geo-derived hitbox pipeline instead of JOML so the math is dependency-free, uses
 * {@link StrictMath} trig (bit-identical on client and dedicated server — both sides compile the
 * same shapes independently and must agree), and composes exactly like a {@code PoseStack}:
 * every operation <b>post-multiplies</b>, so calls read in the same order as GeckoLib's render code
 * ({@code translate(pivot) → rotateZYX(...) → translate(-pivot)}).
 * <p>
 * Rotation matrices are the standard right-handed ones, matching {@code com.mojang.math.Axis}/JOML
 * (verified by {@code GeoTransformParityTest} against the real JOML classes).
 */
public final class Affine3 {

	private static final Affine3 IDENTITY = new Affine3(
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0);

	private final double m00, m01, m02, m03;
	private final double m10, m11, m12, m13;
	private final double m20, m21, m22, m23;

	private Affine3(double m00, double m01, double m02, double m03,
					double m10, double m11, double m12, double m13,
					double m20, double m21, double m22, double m23) {
		this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
		this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
		this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
	}

	public static Affine3 identity() {
		return IDENTITY;
	}

	/** {@code this · other} — the equivalent of applying {@code other} "inside" this transform. */
	public Affine3 mul(Affine3 o) {
		return new Affine3(
				m00 * o.m00 + m01 * o.m10 + m02 * o.m20,
				m00 * o.m01 + m01 * o.m11 + m02 * o.m21,
				m00 * o.m02 + m01 * o.m12 + m02 * o.m22,
				m00 * o.m03 + m01 * o.m13 + m02 * o.m23 + m03,

				m10 * o.m00 + m11 * o.m10 + m12 * o.m20,
				m10 * o.m01 + m11 * o.m11 + m12 * o.m21,
				m10 * o.m02 + m11 * o.m12 + m12 * o.m22,
				m10 * o.m03 + m11 * o.m13 + m12 * o.m23 + m13,

				m20 * o.m00 + m21 * o.m10 + m22 * o.m20,
				m20 * o.m01 + m21 * o.m11 + m22 * o.m21,
				m20 * o.m02 + m21 * o.m12 + m22 * o.m22,
				m20 * o.m03 + m21 * o.m13 + m22 * o.m23 + m23);
	}

	/** Post-multiplies a translation, like {@code PoseStack.translate}. */
	public Affine3 translate(double x, double y, double z) {
		return new Affine3(
				m00, m01, m02, m00 * x + m01 * y + m02 * z + m03,
				m10, m11, m12, m10 * x + m11 * y + m12 * z + m13,
				m20, m21, m22, m20 * x + m21 * y + m22 * z + m23);
	}

	/**
	 * Post-multiplies Z, then Y, then X rotations — the exact call sequence of GeckoLib's
	 * {@code RenderUtil.rotateMatrixAroundBone}/{@code rotateMatrixAroundCube}
	 * ({@code mulPose(Z); mulPose(Y); mulPose(X)}). Angles in radians.
	 */
	public Affine3 rotateZYX(double rz, double ry, double rx) {
		return mul(rotationZ(rz)).mul(rotationY(ry)).mul(rotationX(rx));
	}

	/** Post-multiplies a rotation about +Y, like {@code mulPose(Axis.YP.rotation(angle))}. */
	public Affine3 rotateY(double angleRad) {
		return mul(rotationY(angleRad));
	}

	private static Affine3 rotationX(double a) {
		if (a == 0) return IDENTITY;
		double c = StrictMath.cos(a), s = StrictMath.sin(a);
		return new Affine3(
				1, 0, 0, 0,
				0, c, -s, 0,
				0, s, c, 0);
	}

	private static Affine3 rotationY(double a) {
		if (a == 0) return IDENTITY;
		double c = StrictMath.cos(a), s = StrictMath.sin(a);
		return new Affine3(
				c, 0, s, 0,
				0, 1, 0, 0,
				-s, 0, c, 0);
	}

	private static Affine3 rotationZ(double a) {
		if (a == 0) return IDENTITY;
		double c = StrictMath.cos(a), s = StrictMath.sin(a);
		return new Affine3(
				c, -s, 0, 0,
				s, c, 0, 0,
				0, 0, 1, 0);
	}

	/** General affine inverse (3x3 adjugate + back-transformed translation). */
	public Affine3 invert() {
		double det = m00 * (m11 * m22 - m12 * m21)
				- m01 * (m10 * m22 - m12 * m20)
				+ m02 * (m10 * m21 - m11 * m20);
		double id = 1.0 / det;

		double i00 = (m11 * m22 - m12 * m21) * id;
		double i01 = (m02 * m21 - m01 * m22) * id;
		double i02 = (m01 * m12 - m02 * m11) * id;
		double i10 = (m12 * m20 - m10 * m22) * id;
		double i11 = (m00 * m22 - m02 * m20) * id;
		double i12 = (m02 * m10 - m00 * m12) * id;
		double i20 = (m10 * m21 - m11 * m20) * id;
		double i21 = (m01 * m20 - m00 * m21) * id;
		double i22 = (m00 * m11 - m01 * m10) * id;

		return new Affine3(
				i00, i01, i02, -(i00 * m03 + i01 * m13 + i02 * m23),
				i10, i11, i12, -(i10 * m03 + i11 * m13 + i12 * m23),
				i20, i21, i22, -(i20 * m03 + i21 * m13 + i22 * m23));
	}

	/** Transforms the point into {@code out} (length 3). Allocation-free for the voxelizer's hot loop. */
	public void transform(double x, double y, double z, double[] out) {
		out[0] = m00 * x + m01 * y + m02 * z + m03;
		out[1] = m10 * x + m11 * y + m12 * z + m13;
		out[2] = m20 * x + m21 * y + m22 * z + m23;
	}
}
