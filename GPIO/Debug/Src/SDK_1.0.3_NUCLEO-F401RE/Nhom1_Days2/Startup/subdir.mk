################################################################################
# Automatically-generated file. Do not edit!
# Toolchain: GNU Tools for STM32 (13.3.rel1)
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
S_SRCS += \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/startup_stm32f401retx.s 

OBJS += \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/startup_stm32f401retx.o 

S_DEPS += \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/startup_stm32f401retx.d 


# Each subdirectory must supply rules for building sources it contributes
Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/%.o: ../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/%.s Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/subdir.mk
	arm-none-eabi-gcc -mcpu=cortex-m4 -g3 -DDEBUG -c -x assembler-with-cpp -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" --specs=nano.specs -mfpu=fpv4-sp-d16 -mfloat-abi=hard -mthumb -o "$@" "$<"

clean: clean-Src-2f-SDK_1-2e-0-2e-3_NUCLEO-2d-F401RE-2f-Nhom1_Days2-2f-Startup

clean-Src-2f-SDK_1-2e-0-2e-3_NUCLEO-2d-F401RE-2f-Nhom1_Days2-2f-Startup:
	-$(RM) ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/startup_stm32f401retx.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Startup/startup_stm32f401retx.o

.PHONY: clean-Src-2f-SDK_1-2e-0-2e-3_NUCLEO-2d-F401RE-2f-Nhom1_Days2-2f-Startup

