/**
 * Effective codec configuration bridge.
 * <p>
 * This package provides the {@link org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig}
 * class that bridges the new API config system ({@link org.eclipse.fennec.codec.config.ConfigurationResolver})
 * with the codec context system. It is the single entry point for serializers and deserializers
 * to access resolved configuration.
 * </p>
 *
 * @see org.eclipse.fennec.codec.config.ConfigurationResolver
 * @see org.eclipse.fennec.codec.config.TypeConfig
 * @see org.eclipse.fennec.codec.config.IdConfig
 * @see org.eclipse.fennec.codec.config.FeatureConfig
 */
@org.osgi.annotation.bundle.Export
package org.eclipse.fennec.codec.config.effective;
