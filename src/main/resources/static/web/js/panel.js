const app = Vue.createApp({
    data() {
        return {
            // Mensaje por defecto mientras carga
            welcomeMessage: "Bienvenido, Organizador...", 
            isError: false // Para manejar errores de sesión
        };
    },
    mounted() {
        // Al cargar el componente, intentamos obtener los datos del usuario
        this.fetchUserData();
    },
    methods: {
        fetchUserData() {
            // Llama al endpoint protegido para obtener el nombre del usuario logueado
            axios.get('/api/usuarios/actual', { withCredentials: true })
                .then(response => {
                    const data = response.data;
                    // Asume que el backend devuelve 'nombre'
                    const userName = `${data.nombre.charAt(0).toUpperCase()}${data.nombre.slice(1).toLowerCase()}`;
                    
                    // Actualiza el mensaje con el nombre real
                    this.welcomeMessage = `¡Bienvenido, ${userName}!`;
                    this.isError = false; 
                })
                .catch(error => {
                    console.error("Sesión inválida o expirada:", error);
                    this.welcomeMessage = "Sesión expirada. Por favor, inicie sesión.";
                    this.isError = true;
                    
                    // Si el error indica que la sesión no es válida (código 401), redirigimos
                    if (error.response && error.response.status === 401) {
                         setTimeout(() => {
                            window.location.href = "login.html";
                        }, 1000);
                    }
                });
        },

        handleLogout: function() {
            // Llama a la API de logout de Spring Security
            axios.post('/api/logout', {}, { withCredentials: true })
                .then(() => {
                    // Si el logout es exitoso, redirige al login
                    window.location.href = "login.html";
                })
                .catch(error => {
                    console.error("Error al cerrar sesión:", error);
                    // Si hay un error, redirige al login de todas formas
                    window.location.href = "login.html";
                });
        }



        
    }
});

// Montar la aplicación Vue en el contenedor principal del panel
app.mount('#panelApp');