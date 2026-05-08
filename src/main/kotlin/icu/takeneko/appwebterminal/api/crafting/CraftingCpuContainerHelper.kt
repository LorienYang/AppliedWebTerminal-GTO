package icu.takeneko.appwebterminal.api.crafting

import appeng.api.networking.crafting.ICraftingCPU

object CraftingCpuContainerHelper {
    private val constructors =
        mutableMapOf<Class<out ICraftingCPU>, CraftingCpuContainer.Constructor<ICraftingCPU>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> registerConstructor(
        clazz: Class<T>,
        constructor: CraftingCpuContainer.Constructor<T>
    ) where T : ICraftingCPU {
        constructors.put(clazz, constructor as CraftingCpuContainer.Constructor<ICraftingCPU>)
    }

    /**
     * 创建 CPU 容器，支持父类匹配
     * 如果找不到精确匹配，会查找已注册的父类或接口
     */
    fun create(
        instance: ICraftingCPU
    ): CraftingCpuContainer? {
        val clazz = instance.javaClass as Class<out ICraftingCPU>
        
        // 精确匹配
        constructors[clazz]?.let { return it.create(instance) }
        
        // 父类/接口匹配（支持 AE2GTO 等非官方实现）
        for ((registeredClass, constructor) in constructors) {
            if (registeredClass.isAssignableFrom(clazz)) {
                return constructor.create(instance)
            }
        }
        
        return null
    }

}
