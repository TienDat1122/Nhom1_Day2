 /* File name:
 *
 * Description:
 *
 *
 * Last Changed By:  $Author: $
 * Revision:         $Revision: $
 * Last Changed:     $Date: $july 27, 2022
 *
 * Code sample:
 ******************************************************************************/
/******************************************************************************/
/*                              INCLUDE FILES                                 */
/******************************************************************************/
#include <stdint.h>
#include "stm32f401re_rcc.h"
#include "stm32f401re_gpio.h"
#include "stm32f401re_syscfg.h"
#include "stm32f401re_exti.h"
#include "misc.h"


/******************************************************************************/
/*                     PRIVATE TYPES and DEFINITIONS                         */
/******************************************************************************/
#define GPIO_PIN_SET				1
#define GPIO_PIN_RESET				0
#define GPIO_PIN_HIGH				1
#define GPIO_PIN_LOW				0

#define LED_GPIO_PORT				GPIOA
#define LED_GPIO_PIN				GPIO_Pin_0
#define LEDControl_SetClock			RCC_AHB1Periph_GPIOA

#define BUTTON_GPIO_PORT			GPIOB
#define BUTTON_GPIO_PIN				GPIO_Pin_3
#define BUTTONControl_SetClock		RCC_AHB1Periph_GPIOB

#define SYSCFG_Clock				RCC_APB2Periph_SYSCFG
uint8_t Status = 0;

/******************************************************************************/
/*                     EXPORTED TYPES and DEFINITIONS                         */
/******************************************************************************/


/******************************************************************************/
/*                              PRIVATE DATA                                  */
/******************************************************************************/

/******************************************************************************/
/*                              EXPORTED DATA                                 */
/******************************************************************************/

/******************************************************************************/
/*                            PRIVATE FUNCTIONS                               */
/******************************************************************************/
static void Led_Init(void);
static void delay(void);
static void Interrupt_Init(void);
static void LedControl_Status(GPIO_TypeDef *GPIOx, uint16_t GPIO_PIN, uint8_t Status);
/******************************************************************************/
/*                            EXPORTED FUNCTIONS                              */
/******************************************************************************/
void EXTI3_IRQHandler(void)
{
	if(EXTI_GetFlagStatus(EXTI_Line3) == SET)
	{
		Status = !Status;
	}
	EXTI_ClearITPendingBit(EXTI_Line3);
}
/******************************************************************************/


int main()
{
	SystemCoreClockUpdate();
	Led_Init();
	Interrupt_Init();
	while(1)
	{
		if(Status == 0)
		{
			delay();
			LedControl_Status(LED_GPIO_PORT, LED_GPIO_PIN, GPIO_PIN_LOW);
		}
		else
		{
			LedControl_Status(LED_GPIO_PORT, LED_GPIO_PIN, GPIO_PIN_HIGH);
		}

	}
	return 0;
}

static void Led_Init(void)
{
	GPIO_InitTypeDef	GPIO_InitStructure;

	RCC_AHB1PeriphClockCmd(LEDControl_SetClock, ENABLE);

	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_OUT;
	GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
	GPIO_InitStructure.GPIO_Pin = LED_GPIO_PIN;
	GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_DOWN;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;

	GPIO_Init(LED_GPIO_PORT, &GPIO_InitStructure);
}

static void Interrupt_Init(void)
{
	GPIO_InitTypeDef	GPIO_InitStructure;
	EXTI_InitTypeDef	EXTI_InitStructure;
	NVIC_InitTypeDef	NVIC_InitStructure;

	RCC_AHB1PeriphClockCmd(BUTTONControl_SetClock, ENABLE);

	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN;
	GPIO_InitStructure.GPIO_Pin = BUTTON_GPIO_PIN;
	GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;

	GPIO_Init(BUTTON_GPIO_PORT, &GPIO_InitStructure);

	RCC_APB2PeriphClockCmd(RCC_APB2Periph_SYSCFG, ENABLE);
	SYSCFG_EXTILineConfig(EXTI_PortSourceGPIOB, EXTI_PinSource3);

	EXTI_InitStructure.EXTI_Line = EXTI_Line3;
	EXTI_InitStructure.EXTI_LineCmd = ENABLE;
	EXTI_InitStructure.EXTI_Mode = EXTI_Mode_Interrupt;
	EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Falling;

	EXTI_Init(&EXTI_InitStructure);

	NVIC_InitStructure.NVIC_IRQChannel = EXTI3_IRQn;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;
	NVIC_Init(&NVIC_InitStructure);
}

static void delay(void)
{
	for(uint32_t i =0; i < 500000; i++);
}

static void LedControl_Status(GPIO_TypeDef *GPIOx, uint16_t GPIO_PIN, uint8_t Status)
{
	if(Status == GPIO_PIN_SET)
	{
		GPIOx->BSRRL = GPIO_PIN;
	}
	if(Status == GPIO_PIN_RESET)
	{
		GPIOx->BSRRH = GPIO_PIN;
	}
}
