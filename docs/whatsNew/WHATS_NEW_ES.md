# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Supported section titles:
- Agregado, Cambiado, Arreglado, Removido

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

## [1.3 (812)] - 2024-12-19

### Added
- New feature: Keystone integration with an ability to connect HW wallet to Zashi wallet, preview transactions, sign
  new transactions and shield transparent funds
- Thus, several new screens for the Keystone account import and signing transactions using the Keystone device have
  been added

### Changed
- App bar has been redesigned to give users ability to switch between wallet accounts
- The Integrations screen is now enabled for the Zashi account only
- The Address book screen now shows the wallet addresses if more than one Account is imported
- Optimizations on the New wallet creation to prevent indeterministic chain of async actions
- Optimizations on the Wallet restoration to prevent indeterministic chain of async actions
- Optimizations on the Send screen to run actions on ViewModel scope to prevent actions from being interrupted by
  finalized UI scope
- `SynchronizerProvider` is now the single source of truth when providing synchronizer
- `SynchronizerProvider` provides synchronizer only when it is fully initialized

### Fixed
- Wallet creation and restoration are now more stable for troublesome devices

## [1.2.3 (799)] - 2024-11-26

### Añadido
- ¡Finalmente está aquí! ¡La integración de Flexa a tu servicio!
- Paga con Flexa en comercios compatibles en Estados Unidos, Canadá y El Salvador.
- Te está esperando en la configuración de Zashi.

## [1.2.2 (789)] - 2024-11-18

### Añadido
- ¡Hola!, Zashi aprendió Español.
- Nuevo manejo de transacciones de baja denominación, esto hace transacciones más rápidas y uso eficiente del saldo.
- Implementación de encriptado  y almacenamiento remoto de información en agenda.
- Se agrego una barra de progreso y nuevas pantallas de éxito y falla.
- Inicio de aplicación con autenticación implementada.

### Cambiado
- Mejora en pantalla de configuración y estatus.
- Comentarios y sugerencias mejoradas.

### Corregido
- Icono de transacciones privadas arreglado.
