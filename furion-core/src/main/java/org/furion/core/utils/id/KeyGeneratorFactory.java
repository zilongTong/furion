package org.furion.core.utils.id;

public class KeyGeneratorFactory {


    public static KeyGenerator build(GeneratorEnum type) {
        if (GeneratorEnum.HOST.equals(type)) {
            return new HostNameKeyGenerator();
        }
        if (GeneratorEnum.IP.equals(type)) {
            return new IPKeyGenerator();
        }
        if (GeneratorEnum.IP_SECTION.equals(type)) {
            return new IPSectionKeyGenerator();
        }
        return new IPKeyGenerator();
    }

}
