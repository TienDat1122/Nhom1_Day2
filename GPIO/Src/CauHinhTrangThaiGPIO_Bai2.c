#include <stdint.h>
#include <stm32f401re_gpio.h>
#include <stm32f401re_rcc.h>


#define GPIO_PIN_SET    1
#define GPIO_PIN_RESET  0
#define BTN_PRESS       0


#define BUZZER_GPIO_PORT   GPIOC
#define BUZZER_GPIO_PIN    GPIO_Pin_9
#define BUZZER_CLOCK       RCC_AHB1Periph_GPIOC

#define BUTTON_GPIO_PORT GPIOB
#define BUTTON_GPIO_PIN  GPIO_Pin_4
#define BUTTON_CLOCK     RCC_AHB1Periph_GPIOB


void delay(void) {
    for (uint32_t i = 0; i < 50000; i++);
}


static void Buzzer_init(void) {
    GPIO_InitTypeDef GPIO_InitStructure;
    RCC_AHB1PeriphClockCmd(BUZZER_CLOCK, ENABLE);
    GPIO_InitStructure.GPIO_Pin = BUZZER_GPIO_PIN;
    GPIO_InitStructure.GPIO_Mode = GPIO_Mode_OUT;
    GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
    GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
    GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL;
    GPIO_Init(BUZZER_GPIO_PORT, &GPIO_InitStructure);
}


static void Button_init(void) {
    GPIO_InitTypeDef GPIO_InitStructure;
    RCC_AHB1PeriphClockCmd(BUTTON_CLOCK, ENABLE);
    GPIO_InitStructure.GPIO_Pin = BUTTON_GPIO_PIN;
    GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
    GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
    GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;  // Pull-up cho nút
    GPIO_Init(BUTTON_GPIO_PORT, &GPIO_InitStructure);
}


static void Buzzer_SetStatus(GPIO_TypeDef *GPIOx, uint16_t GPIO_PIN, uint8_t Status) {
    if (Status == GPIO_PIN_SET) {
        GPIOx->BSRRL = GPIO_PIN;
    } else {
        GPIOx->BSRRH = GPIO_PIN;
    }
}


static uint8_t ButtonRead_Status(GPIO_TypeDef *GPIOx, uint16_t GPIO_PIN) {
    return (GPIOx->IDR & GPIO_PIN) ? GPIO_PIN_SET : GPIO_PIN_RESET;
}

int main(void) {
    Buzzer_init();
    Button_init();

    uint8_t buzzer_state = GPIO_PIN_RESET;
    uint8_t last_button_state = GPIO_PIN_SET;

    while (1) {
        uint8_t current_button_state = ButtonRead_Status(BUTTON_GPIO_PORT, BUTTON_GPIO_PIN);


        if (current_button_state == BTN_PRESS && last_button_state == GPIO_PIN_SET) {

            buzzer_state = (buzzer_state == GPIO_PIN_SET) ? GPIO_PIN_RESET : GPIO_PIN_SET;
            Buzzer_SetStatus(BUZZER_GPIO_PORT, BUZZER_GPIO_PIN, buzzer_state);
            delay();
        }

        last_button_state = current_button_state;
    }
    return 0;
}
