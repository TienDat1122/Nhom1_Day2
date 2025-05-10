################################################################################
# Automatically-generated file. Do not edit!
# Toolchain: GNU Tools for STM32 (13.3.rel1)
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai1.c \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai2.c \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai3.c \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai1.c \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai2.c \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/syscalls.c \
../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/sysmem.c 

OBJS += \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai1.o \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai2.o \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai3.o \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai1.o \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai2.o \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/syscalls.o \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/sysmem.o 

C_DEPS += \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai1.d \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai2.d \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai3.d \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai1.d \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai2.d \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/syscalls.d \
./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/sysmem.d 


# Each subdirectory must supply rules for building sources it contributes
Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/%.o Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/%.su Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/%.cyclo: ../Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/%.c Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/subdir.mk
	arm-none-eabi-gcc "$<" -mcpu=cortex-m4 -std=gnu11 -g3 -DDEBUG -DNUCLEO_F401RE -DSTM32 -DSTM32F401RETx -DSTM32F4 -c -I../Inc -O0 -ffunction-sections -fdata-sections -Wall -fstack-usage -fcyclomatic-complexity -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" --specs=nano.specs -mfpu=fpv4-sp-d16 -mfloat-abi=hard -mthumb -o "$@"

clean: clean-Src-2f-SDK_1-2e-0-2e-3_NUCLEO-2d-F401RE-2f-Nhom1_Days2-2f-Src

clean-Src-2f-SDK_1-2e-0-2e-3_NUCLEO-2d-F401RE-2f-Nhom1_Days2-2f-Src:
	-$(RM) ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai1.cyclo ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai1.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai1.o ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai1.su ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai2.cyclo ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai2.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai2.o ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai2.su ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai3.cyclo ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai3.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai3.o ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/CauHinhTrangThaiGPIO_Bai3.su ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai1.cyclo ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai1.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai1.o ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai1.su ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai2.cyclo ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai2.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai2.o ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/Interrupt_Bai2.su ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/syscalls.cyclo ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/syscalls.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/syscalls.o ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/syscalls.su ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/sysmem.cyclo ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/sysmem.d ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/sysmem.o ./Src/SDK_1.0.3_NUCLEO-F401RE/Nhom1_Days2/Src/sysmem.su

.PHONY: clean-Src-2f-SDK_1-2e-0-2e-3_NUCLEO-2d-F401RE-2f-Nhom1_Days2-2f-Src

