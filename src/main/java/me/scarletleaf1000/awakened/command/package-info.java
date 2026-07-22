/**
 * Modular, data-driven Command subsystem for Awakened.
 *
 * <p>A Command is built from:
 * <ul>
 *   <li>one {@link Trigger} (when it activates)</li>
 *   <li>one {@link Action} (what happens)</li>
 *   <li>one {@link Target} (who it affects)</li>
 * </ul>
 *
 * <p><b>Adding a new Trigger, Action, or Target</b>
 * <ol>
 *   <li>Create a class implementing {@link Trigger}, {@link Action}, or {@link Target}.</li>
 *   <li>Register it in {@link CommandRegistries} under the matching deferred register.</li>
 *   <li>You can also register from any other class via the same static fields;
 *       see {@link me.scarletleaf1000.awakened.command.debug.ExtraTriggers}.</li>
 *   <li>Build a Command with {@link CommandBuilder#build(ResourceLocation, ResourceLocation, ResourceLocation, int)}.</li>
 * </ol>
 *
 * <p>Every registry entry extends {@link TieredEntry}, which carries a {@code minTier}
 * integer. The builder validates that the player's available tier is high enough; registry
 * listing code should also filter by this field.
 */
package me.scarletleaf1000.awakened.command;
