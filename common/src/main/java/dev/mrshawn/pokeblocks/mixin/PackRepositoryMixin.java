package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.resourcepack.resources.PokedollsInMemoryPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

    @Shadow
    private Map<String, Pack> available;

    @Shadow
    private List<Pack> selected;

    @Inject(method = "reload", at = @At("TAIL"))
    private void pokeblocks$injectGeneratedPack(CallbackInfo ci) {
        Pack pack = PokedollsInMemoryPack.createPack();
        if (pack != null) {
            Map<String, Pack> mutableAvailable = new TreeMap<>(this.available);
            mutableAvailable.put(pack.getId(), pack);
            this.available = mutableAvailable;

            List<Pack> mutableSelected = new ArrayList<>(this.selected);
            if (!mutableSelected.contains(pack)) {
                mutableSelected.addFirst(pack);
            }
            this.selected = mutableSelected;
        }
    }
}