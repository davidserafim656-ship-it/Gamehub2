# Roblox DPI

Aplicativo Android simples para aplicar 600 DPI com Shizuku, abrir o Roblox e
restaurar a densidade original depois.

## Uso

1. Inicie o Shizuku no celular.
2. Abra o Roblox DPI e toque em **Conectar Shizuku**.
3. Autorize o aplicativo no Shizuku quando solicitado.
4. Toque em **Abrir Roblox em 600 DPI**.
5. Quando terminar de jogar, volte ao Roblox DPI e toque em
   **Restaurar DPI normal**.

O aplicativo altera somente a densidade lógica do display. Ele não muda a
resolução, as escalas de animação nem outras configurações do Android.

## Build

```sh
./gradlew clean assembleDebug
```

O APK é criado em `app/build/outputs/apk/debug/app-debug.apk`.
