package io.mctest.agent.core;

import java.util.Map;

/**
 * Source-compatible stand-in for mc-test's server-truth SPI, vendored so this repository builds
 * without a local install of {@code io.mctest:mc-test-agent-core} (see this module's build script).
 *
 * <p>A mod implements this interface and declares the implementation in
 * {@code META-INF/services/io.mctest.agent.core.McTestStateProvider}; the mc-test server agent — which
 * bundles the real {@code mc-test-agent-core} — discovers it with {@link java.util.ServiceLoader} and
 * calls {@link #query} on the server thread to answer {@code truth.assertPluginState} steps.
 *
 * <p>This copy exists for compilation only and must never reach a runtime classpath: two copies of the
 * interface would break ServiceLoader class identity and the agent would not see the provider.
 *
 * <p>Pokeblocks' implementation is {@code dev.mrshawn.pokeblocks.mctest.PokeblocksStateProvider}.
 */
public interface McTestStateProvider {

    /**
     * Answers one state query with a pure value (a primitive, {@code String} or other simple object);
     * the agent applies the test's {@code expect} predicate to the result.
     *
     * @param query the query name, e.g. {@code "item.exists"}
     * @param args  the query's arguments, as parsed from the test step
     * @return the queried value
     * @throws Exception if the query is unknown or its arguments are invalid
     */
    Object query(String query, Map<String, Object> args) throws Exception;
}
