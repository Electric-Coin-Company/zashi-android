# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Supported section titles:
- Agregado, Cambiado, Arreglado, Removido

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

### Changed
- The QR code image logic of the `QrCode`, `Request`, and `SignTransaction` screens has been refactored to work
  with the newer `ZashiQr` component
- The colors of the QR code image on the `SignTransaction` screen are now white and black for both color themes to
  improve the successful scanning chance by the Keystone device
- The block synchronization progress logic has been changed to return an uncompleted percentage in case the
  `Synchronizer` is still in the `SYNCING` state

### Fixed
- The Disconnected popup trigger when the app is backgrounded has been fixed
- The issue when the application does not clean navigation stack after clicking View Transactions after a successful
  transaction has been resolved

## [1.3.2 (829)] - 2025-01-10

### Cambiado
- Zashi ahora muestra la versión oscura de los códigos QR en el tema oscuro.
- Mejoramos el escáner de códigos QR para que sea más rápido.
- Reestructuramos las pantallas de Enviar para optimizar su funcionamiento.

### Corregido
- Solucionamos la manera en que Zashi procesa las direcciones contenidas en los códigos QR.

## [1.3.1 (822)] - 2025-01-07

### Corregido
- Hemos corregido un bug en Coinbase Onramp que había impactando a nuestros usuarios cuando hacen compras con sus 
  cuentas de Coinbase. Ahora pasamos la dirección pública correcta a Coinbase y tus ZEC son enviados directamente a 
  tu Billetera Zashi en lugar de tu cuenta de Coinbase.

## [1.3 (812)] - 2024-12-19

### Añadido
La integración de Zashi con la Billetera Física Keystone esta ya disponible!
- Conecta to Billetera Fisica Keystone con Zashi.
- Firma tus transacciones con tu Billetera Keystone.
- Incluye soporte para ambos ZEC transparentes y protegidos. 

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
