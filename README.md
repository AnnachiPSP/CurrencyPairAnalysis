# Crypto Currency Analysis

This application's purpose is to monitor and generate payload simulations for a chosen crypto-currency pair.


## Techs Used

- Java (Gradle)
- Android Studio
- WazirX API


## Usage

This project can be run using android studio / visual studio / IntelliJ with either an emulator or your mobile phone. Steps that can be perfored are mentioned below
OR
You can deirectly download the [apk file](https://github.com/AnnachiPSP/CurrencyPairAnalysis/blob/master/app-currencyPair.apk) on your mobile phone 

### 1. Home Screen

You would have to wait for few milli seconds as the data of the available currency pairs are retreived in realtime. Once loaded you can choose the pair you wish to proceed with

![alt text](https://github.com/AnnachiPSP/CurrencyPairAnalysis/blob/master/img/home_screen.jpg)

### 2. Action Screen

After selecting the pair you wish to proceed with, in the next page, the current price of conversion will be visible and you there you will have to enter a convinient Buy Trigger and Sell Trigger

![alt text](https://github.com/AnnachiPSP/CurrencyPairAnalysis/blob/master/img/action_page.jpg)

### 3. Aalysis Page

Once the triggers are set, you'll be able to visualize the price/time graph where time is in MM/dd hh:mm format, triggers are represented as green dotted lines

![alt text](https://github.com/AnnachiPSP/CurrencyPairAnalysis/blob/master/img/analysis_page.jpg)

### 4. Payload Generation

The payloads are generated and displayed as alert message upon 3 events

- Whenever the price hits or exceeds sell trigger, sell payload would be generated
- Whenever the price drops below buy trigger, buy payload would be generated
- Whenever the Cancel button is clicked, the simulation is cancelled. The screen is redirected to Main Screen 2 seconds after the cancel payload is generated

![alt text](https://github.com/AnnachiPSP/CurrencyPairAnalysis/blob/master/img/payload.jpg)
