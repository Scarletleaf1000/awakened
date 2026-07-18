/**
 * Modular, data-driven Command subsystem for Awakened.
 *
 * <p>A Command is built from:
 * <ul>
 *   <li>one {@link Trigger} (when it activates)</li>
 *   <li>one {@link Action} (what happens)</li>
 *   <li>an optional {@link Condition} (filter on the trigger)</li>
 * </ul>
 *
 * <p><b>Adding a new Trigger or Action</b>
 * <ol>
 *   <li>Create a class implementing {@link Trigger} or {@link Action}.</li>
 *   <li>Register it in {@link CommandRegistries}:
 *       <pre>
 *       public static final RegistryObject&lt;Trigger&gt; MY_TRIGGER =
 *           CommandRegistries.TRIGGERS.register("my_trigger", MyTrigger::new);
 *       </pre>
 *       or
 *       <pre>
 *       public static final RegistryObject&lt;Action&gt; MY_ACTION =
 *           CommandRegistries.ACTIONS.register("my_action", MyAction::new);
 *       </pre>
 *   </li>
 *   <li>You can also register from any other class via the same static fields;
 *       see {@link me.scarletleaf1000.awakened.command.debug.ExtraTriggers}.</li>
 *   <li>Build a Command with {@link CommandBuilder#build(ResourceLocation, ResourceLocation, int)}
 *       or the overload that accepts an optional {@link Condition} once Conditions exist.</li>
 * </ol>
 *
 * <p>Every registry entry extends {@link TieredEntry}, which carries a {@code minTier}
 * integer. The builder validates that the player's available tier is high enough; registry
 * listing code should also filter by this field.
 *
 * <p>Conditions are intentionally left as a registry slot with no concrete entries in this
 * starter. The API shape is already wired into {@link Command} and {@link CommandBuilder} so
 * adding the first Condition later will not be a breaking change.
 */
package me.scarletleaf1000.awakened.command;
