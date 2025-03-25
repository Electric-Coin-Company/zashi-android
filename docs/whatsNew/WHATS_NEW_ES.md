# Changelog
All notable changes to this application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this application adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Supported section titles:
- Añadido, Cambiado, Corregido, Removido

**Please be aware that this changelog primarily focuses on user-related modifications, emphasizing changes that can
directly impact users rather than highlighting other key architectural updates.**

## [Unreleased]

## [1.5 (921)] - 2025-03-26

### Añadido:
- Zashi ahora puede ayudarte a recuperar fondos desde billeteras completamente transparentes como Ledger. Recomendamos importar la frase de recuperación de tu billetera hardware transparente en una billetera Keystone y luego emparejarla con Zashi usando la integración con Keystone.

### Corregido:
- Solucionamos un problema de larga data con el árbol de compromisos de notas que afectaba a un pequeño número de usuarios. Ahora Zashi permite gastar fondos que estaban atascados.

## [1.4 (876)] - 2025-03-04

### Añadido
- Nueva funcionalidad ”Exporta Archivo de Impuestos”.
- Transacciones Favoritas y notas personalizadas.
- Filtros para transacciones.
- Descarga Zashi desde F-Droid y GitHub.

### Cambiado
- Rediseño total del Historial de Transacciones.
- Accede a KeyStone desde la pantalla de integraciones.
- Mejoramos la experiencia de firmado de transacciones.

### Corregido
- No más fallos al enviar en KeyStone, han sido arreglados.
- ”Exporta Archivo de Impuestos” también está corregido.

## [1.4 (873)] - 2025-03-03

### Añadido
- Nueva funcionalidad “Exporta Archivo de Impuestos”.
- Transacciones Favoritas y notas personalizadas.
- Filtros para transacciones recibidas, enviadas, favoritas y personalizadas.
- Descarga Zashi Android desde F-Droid y GitHub.

### Cambiado
- Rediseño total del Historial de Transacciones. 
- Accede a KeyStone desde la pantalla de integraciones.
- Mejoramos la experiencia de firmado de transacciones.

### Corregido 
- No más problemas con el envío de KeyStone, lo arreglamos!
  
## [1.3.3 (839)] - 2025-01-23

### Cambiado
- Refactorizamos la lógica de la imagen del código QR para que funcione con el nuevo componente ZashiQr.
- Ahora, los colores de la imagen del código QR en la pantalla de SignTransaction son los mismos en ambos temas de 
  color, mejorando el escaneo por parte del dispositivo Keystone.
- Mejoramos la lógica del progreso de sincronización de bloques para devolver un porcentaje incompleto en caso de que 
  el sincronizador aún esté en el estado SYNCING.
- Actualizamos el SDK de Keystone a la versión 0.7.10, lo que incluye una mejora significativa en el escaneo de 
  códigos QR.

### Corregido
- Corregimos la lógica del popup de Desconectado en los casos en que la aplicación pasa a segundo plano.
- También resolvimos un problema con la pila de navegación de la aplicación, que no se limpiaba correctamente después 
  de hacer clic en el botón Ver Transacciones.

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
