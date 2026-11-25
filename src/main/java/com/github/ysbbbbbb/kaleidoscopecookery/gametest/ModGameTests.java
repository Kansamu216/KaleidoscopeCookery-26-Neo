package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

/**
 * Registers the mod's cooking-recipe GameTests into the vanilla test registry.
 * These run on the built-in {@code empty} structure with the default environment.
 */
@EventBusSubscriber(modid = "kaleidoscope_cookery")
public final class ModGameTests {
   private ModGameTests() {
   }

   /**
    * NeoForge fires this event in every development run because
    * {@code GameTestHooks.isGametestEnabled()} is true whenever the game runs from an IDE,
    * regardless of {@code neoforge.enabledGameTestNamespaces}.
    * <p>
    * Our instances are {@link FunctionInstance}s, a custom {@code GameTestInstance} whose
    * codec cannot be dispatched through the {@code test_instance_type} registry, so they
    * cannot be serialised for registry synchronisation: if they are registered in an
    * ordinary client run, joining a world throws "Unregistered holder ... Direct{Unit[...]}"
    * inside {@code RegistrySynchronization} and the client hangs on "Loading terrain"
    * for ever. Registering only when a run config explicitly asks for gametest namespaces
    * (the {@code gameTestServer} run does) keeps normal client/server runs healthy.
    */
   @SubscribeEvent
   public static void register(RegisterGameTestsEvent event) {
      String namespaces = System.getProperty("neoforge.enabledGameTestNamespaces", "");
      if (namespaces.isBlank()) {
         return;
      }
      Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
         Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "cooking_recipe_empty"),
         new TestEnvironmentDefinition.Functions(java.util.Optional.empty(), java.util.Optional.empty())
      );
      register(event, environment, "pot_recipe_resolves_result", CookingRecipeGameTests::potRecipeResolvesResult);
      register(event, environment, "stockpot_recipe_resolves_result", CookingRecipeGameTests::stockpotRecipeResolvesResult);
      register(event, environment, "millstone_recipe_resolves_result", CookingRecipeGameTests::millstoneRecipeResolvesResult);
      register(event, environment, "chopping_board_recipe_resolves_result", CookingRecipeGameTests::choppingBoardRecipeResolvesResult);
      register(event, environment, "millstone_input_capability_validates_recipes", CookingRecipeGameTests::millstoneInputCapabilityValidatesRecipes);
      register(event, environment, "recipe_count_is_reported", CookingRecipeGameTests::recipeCountIsReported);
      register(event, environment, "chef_has_trades", TradeGameTests::chefHasTrades);
      register(event, environment, "teapot_recipe_resolves_result", CookingRecipeGameTests::teapotRecipeResolvesResult);
      register(event, environment, "steamer_recipe_resolves_result", CookingRecipeGameTests::steamerRecipeResolvesResult);
      register(event, environment, "flex_pot_recipe_resolves_result", CookingRecipeGameTests::flexPotRecipeResolvesResult);
      register(event, environment, "flex_stockpot_recipe_resolves_result", CookingRecipeGameTests::flexStockpotRecipeResolvesResult);
      register(event, environment, "rice_bowl_recipe_resolves_result", CookingRecipeGameTests::riceBowlRecipeResolvesResult);
   }

   private static void register(
      RegisterGameTestsEvent event,
      Holder<TestEnvironmentDefinition<?>> environment,
      String name,
      Consumer<GameTestHelper> function
   ) {
      Identifier id = Identifier.fromNamespaceAndPath("kaleidoscope_cookery", name);
      TestData<Holder<TestEnvironmentDefinition<?>>> data = new TestData<>(
         environment, Identifier.withDefaultNamespace("empty"), 200, 1, true
      );
      event.registerTest(id, new FunctionInstance(data, function, net.minecraft.network.chat.Component.literal(name)));
   }
}
