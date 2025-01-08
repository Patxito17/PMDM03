# Tu Pokédex

## Introducción

**Tu Pokédex** es una aplicación móvil diseñada para capturar y almacenar Pokémon y sus detalles. Permite a los usuarios crear y gestionar una lista personalizada de Pokémon capturados, acceder a la Pokédex, y configurar preferencias de generación Pokémon, tipos de orden, etc. La autenticación de usuarios y almacenamiento de datos se realiza a través de Firebase, y la Pokédex utiliza datos obtenidos directamente de PokeAPI.

---

## Características Principales

- **Autenticación de usuarios:** Inicio de sesión y registro mediante Firebase Authentication, incluyendo soporte para autenticación con Google.
- **Pokédex completa:** Accede a una lista inicial de 150 Pokémon obtenidos de PokeAPI. Resalta los Pokémon capturados y permite seleccionarlos para agregarlos a su lista personal.
- **Gestión de pokémon capturados:** Visualiza y administra los Pokémon capturados con datos detallados como tipo(s), peso y altura. Elimina Pokémon si la opción está habilitada en los ajustes.
- **Ajustes personalizables:** Cambia el idioma entre castellano e inglés, selecciona diferentes tipos de generación Pokémon, habilita o deshabilita la opción para eliminar Pokémon, configura el tipo de orden en el que se muestran los Pokémon capturados y cierra sesión.
- **Estilo visual:** Diseño inspirado en el universo Pokémon con los colores más característicos.

---

## Tecnologías Utilizadas

- **Librerías y Frameworks:**
  - **Firebase Authentication:** Gestión de usuarios.
  - **Firebase Firestore:** Almacenamiento de datos de los Pokémon capturados y de la configuración de cada usuario/a.
  - **Retrofit:** Consumo de la API de Pokémon.
  - **Picasso:** Gestión de imágenes.
  - **RecyclerView y CardView:** Visualización interactiva de listas.
  - **SharedPreferences:** Almacenamiento de configuraciones locales.

- **Lenguaje:** Java.  
- **API Externa:** [PokéAPI](https://pokeapi.co/) para datos de la Pokédex.  
- **Diseño:** Temas personalizados con colores Pokémon.  

---

# Instrucciones para Ejecutar el Proyecto

## Instrucciones de Uso

### 1. Clonar el Repositorio
Clona el repositorio en tu máquina local ejecutando el siguiente comando en la terminal:
```bash
git clone https://github.com/tu_usuario/pokemon-manager-app.git
cd pokemon-manager-app
```

### 2. Configurar Firebase
- Accede a la [consola de Firebase](https://console.firebase.google.com/) y configura un nuevo proyecto si aún no tienes uno.
- Descarga el archivo `google-services.json` desde la configuración de tu proyecto Firebase.
- Coloca el archivo en el directorio `app/` del proyecto para permitir la integración con Firebase.

### 3. Instalar Dependencias
- Asegúrate de tener instalado Android Studio con el SDK de Android y Gradle configurado.
- Abre el proyecto en Android Studio y sincroniza las dependencias seleccionando la opción **"Sync Project with Gradle Files"** en la parte superior de la ventana.

### 4. Ejecutar la Aplicación
- Conecta un dispositivo Android físico con modo de desarrollador habilitado, o configura un emulador Android.
- En Android Studio, selecciona el dispositivo desde el menú desplegable de dispositivos disponibles.
- Haz clic en el botón **"Run"** (o presiona `Shift + F10`) para compilar y ejecutar la aplicación.

### 5. Probar la Aplicación
- Regístrate o inicia sesión con Firebase Authentication.
- Explora las pestañas de la aplicación (Pokédex, Pokémon Capturados y Ajustes) y utiliza sus funcionalidades.

---

## Notas Adicionales
- Asegúrate de que tu archivo `google-services.json` esté correctamente configurado, ya que es esencial para la autenticación y el almacenamiento de datos.
- Si encuentras errores de dependencias, verifica que tengas las versiones compatibles de las bibliotecas mencionadas en el archivo `build.gradle`.
- Consulta la documentación oficial de [Firebase](https://firebase.google.com/docs) o [Android](https://developer.android.com/docs) si necesitas más ayuda.
---

## Conclusiones del Desarrollador

El desarrollo de **Tu Pokédex** ha sido una tarea realmente interesante. La integración de Firebase y Retrofit han sido esenciales en cuanto a la funcionalidad de la app, tratando de tener en cuenta el desarrollo de un diseño amigable con el usuario. Trabajar con API externas y el manejo de datos en tiempo real con Firestore así como aprender a navegar por su docuementación ha sido esencial para poder conseguir completar la aplicación.

## Capturas de pantalla

### Pantalla de Inicio
<img src="screenshots/login.png" alt="Pantalla de Inicio" height="600"/>

### Pantalla de Pokémons capturados
<img src="screenshots/captured.png" alt="Pantalla de Pokémons Capturados" height="600"/>

### Pantalla de los detalles del Pokémon
<img src="screenshots/details.png" alt="Pantalla con detalles de un Pokémon capturado" height="600"/>

### Pantalla de Pokédex
<img src="screenshots/pokedex.png" alt="Pantalla de Pokédex" height="600"/>

### Pantalla de Ajustes
<img src="screenshots/settings.png" alt="Pantalla de Ajustes" height="600"/>
