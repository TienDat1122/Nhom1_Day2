################################################################################
# Automatically-generated file. Do not edit!
# Toolchain: GNU Tools for STM32 (13.3.rel1)
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../Src/CauHinhTrangThaiGPIO_Bai1.c \
../Src/CauHinhTrangThaiGPIO_Bai2.c \
../Src/CauHinhTrangThaiGPIO_Bai3.c \
../Src/syscalls.c \
../Src/sysmem.c 

OBJS += \
./Src/CauHinhTrangThaiGPIO_Bai1.o \
./Src/CauHinhTrangThaiGPIO_Bai2.o \
./Src/CauHinhTrangThaiGPIO_Bai3.o \
./Src/syscalls.o \
./Src/sysmem.o 

C_DEPS += \
./Src/CauHinhTrangThaiGPIO_Bai1.d \
./Src/CauHinhTrangThaiGPIO_Bai2.d \
./Src/CauHinhTrangThaiGPIO_Bai3.d \
./Src/syscalls.d \
./Src/sysmem.d 


# Each subdirectory must supply rules for building sources it contributes
Src/%.o Src/%.su Src/%.cyclo: ../Src/%.c Src/subdir.mk
	arm-none-eabi-gcc "$<" -mcpu=cortex-m4 -std=gnu11 -g3 -DDEBUG -DNUCLEO_F401RE -DSTM32 -DSTM32F401RETx -DSTM32F4 -c -I../Inc -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Middle/button" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Drivers/CMSIS/Include" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Drivers/STM32F401RE_StdPeriph_Driver/inc" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Utilities" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Middle/buzzer" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Middle/led" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Middle/rtos" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Middle/sensor" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Middle/serial" -I"D:/day2/GPIO/Src/SDK_1.0.3_NUCLEO-F401RE/shared/Middle/ucglib" -O0 -ffunction-sections -fdata-sections -Wall -fstack-usage -fcyclomatic-complexity -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" --specs=nano.specs -mfpu=fpv4-sp-d16 -mfloat-abi=hard -mthumb -o "$@"

clean: clean-Src

clean-Src:
	-$(RM) ./Src/CauHinhTrangThaiGPIO_Bai1.cyclo ./Src/CauHinhTrangThaiGPIO_Bai1.d ./Src/CauHinhTrangThaiGPIO_Bai1.o ./Src/CauHinhTrangThaiGPIO_Bai1.su ./Src/CauHinhTrangThaiGPIO_Bai2.cyclo ./Src/CauHinhTrangThaiGPIO_Bai2.d ./Src/CauHinhTrangThaiGPIO_Bai2.o ./Src/CauHinhTrangThaiGPIO_Bai2.su ./Src/CauHinhTrangThaiGPIO_Bai3.cyclo ./Src/CauHinhTrangThaiGPIO_Bai3.d ./Src/CauHinhTrangThaiGPIO_Bai3.o ./Src/CauHinhTrangThaiGPIO_Bai3.su ./Src/syscalls.cyclo ./Src/syscalls.d ./Src/syscalls.o ./Src/syscalls.su ./Src/sysmem.cyclo ./Src/sysmem.d ./Src/sysmem.o ./Src/sysmem.su

.PHONY: clean-Src

