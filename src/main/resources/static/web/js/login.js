const app = Vue.createApp({
    data() {
        return {
            // Datos para Login y Registro
            nombre: "",
            apellido: "",
            email: "",
            password: "",
            rol: "", // Captura la opción del select

            // Estado de la interfaz
            errorMsg: "",
            isSuccess: false,
            loading: false
        };
    },
    methods: {

        // --- LÓGICA DE LOGIN (CU3) ---
        handleLogin: function() {
            this.errorMsg = "";
            this.isSuccess = false;
            this.loading = true;

            const loginData = new URLSearchParams();
            loginData.append('email', this.email);
            loginData.append('password', this.password);

            const config = {
                headers: { 'content-type': 'application/x-www-form-urlencoded' },
                withCredentials: true
            };

            axios.post('http://localhost:8081/api/login', loginData, config)
                .then(() => {
                    this.loading = false;
                    // Redirección temporal (mañana lo hacemos dual según el rol)
                    window.location.href = "O_bienvenido.html";
                })
                .catch(error => {
                    this.loading = false;
                    this.errorMsg = "Credenciales incorrectas o usuario no existe.";
                    this.password = "";
                });
        },

        // --- LÓGICA DE REGISTRO DUAL (CU2) ---
        handleRegister: function() {
            this.errorMsg = "";
            this.isSuccess = false;
            this.loading = true;

            const registerData = {
                nombre: this.nombre,
                apellido: this.apellido,
                email: this.email,
                password: this.password,
                rol: this.rol
            };

            axios.post('/api/auth/registrar', registerData)
                .then(response => {
                    this.loading = false;
                    this.isSuccess = true;
                    this.errorMsg = response.data + ". ¡Redirigiendo!";
                    this.resetForm();

                    setTimeout(() => {
                        this.goToLogin();
                    }, 2000);
                })
                .catch(error => {
                    this.loading = false;
                    this.isSuccess = false;
                    let msg = "Error de registro. Verifique sus datos.";

                    if (error.response && error.response.data) {
                        msg = error.response.data;
                    }

                    console.error("Registro Fallido:", error);
                    this.errorMsg = msg;
                });
        },

        // --- FUNCIONES DE UTILIDAD Y NAVEGACIÓN ---
        resetForm: function() {
            this.nombre = "";
            this.apellido = "";
            this.email = "";
            this.password = "";
            this.rol = "";
        },

        goToLogin: function() {
             window.location.href = "login.html";
        },

        // ESTA ES LA FUNCIÓN QUE FALTABA
        goToRegister: function() {
            window.location.href = "registro.html";
        }
    }
});

app.mount('#app');