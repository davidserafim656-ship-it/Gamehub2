# Game Hub 2.0

App Android simples com:

- Overlay por cima do Roblox
- Botão para baixar resolução via Shizuku
- Botão para restaurar resolução normal
- Abrir e fechar Roblox
- Medidor simples de RAM livre
- Build automática via GitHub Actions

## Requisitos no celular

1. Instalar Shizuku
2. Iniciar Shizuku
3. Abrir Game Hub 2.0
4. Tocar em "Pedir permissão Shizuku"
5. Tocar em "Permitir overlay"
6. Tocar em "Iniciar overlay"

## Comandos usados

Modo desempenho:

```sh
wm size 720x1600
wm density 240
```

Restaurar:

```sh
wm size reset
wm density reset
```

Abrir Roblox:

```sh
monkey -p com.roblox.client 1
```

Fechar Roblox:

```sh
am force-stop com.roblox.client
```

## Build

No GitHub:

Actions → Build APK → Run workflow

Depois baixe o APK em Artifacts.
