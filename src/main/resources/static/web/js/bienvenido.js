const app = Vue.createApp({
    data() {
        return {
            welcomeMessage: "Verificando sesión...",
            buttonText: "Cargando...",
            userName: "",
            userRole: "",
            isError: false
        };
    },
    mounted() {
        this.fetchUserData();
    },
    methods: {
        fetchUserData() {
            // Llamamos al endpoint que ya tiene el instanceof en Java
            axios.get('/api/usuarios/actual', { withCredentials: true })
                .then(response => {
                    const data = response.data;
                    this.userName = data.nombre;
                    this.userRole = data.rol; // "ORGANIZADOR" o "PARTICIPANTE"

                    this.welcomeMessage = `¡Bienvenido, ${this.userName}!`;
                    this.isError = false;

                    // Ajustamos el texto del botón según el rol detectado
                    if (this.userRole === 'ORGANIZADOR') {
                        this.buttonText = "Ir al Panel de Gestión";
                    } else {
                        this.buttonText = "Ver Eventos Disponibles";
                    }
                })
                .catch(error => {
                    console.error("Error de sesión:", error);
                    this.welcomeMessage = "Tu sesión ha expirado o es inválida.";
                    this.isError = true;
                });
        },

        // --- MÉTODO DE REDIRECCIÓN DUAL ---
        goToManagement: function() {
            if (this.userRole === 'ORGANIZADOR') {
                // El organizador va a su panel de control
                window.location.href = "O_panel.html";
            } else {
                // El participante va a la lista de eventos públicos
                window.location.href = "mod_eventos.html";
            }
        },
        
        goToLogin: function() {
            window.location.href = "login.html";
        }
    }
});

app.mount('#app');