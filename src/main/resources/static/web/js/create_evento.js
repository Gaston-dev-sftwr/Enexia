const app = Vue.createApp({
    data() {
        return {
            // Campos del formulario (v-model)
            nombre: '',
            categoria: '',
            fechaInicio: '',
            fechaFin: '',
            hora: null,
            direccion: '',
            descripcion: '',

            // Nuevo: Archivo de imagen
            file: null,

            // Estado de la UI
            errorMsg: '',
            isSuccess: false,
            loading: false // Para mostrar el spinner en el botón
        };
    },

    mounted() {
        const vm = this;

        // Inicializa Flatpickr (Calendario)
        flatpickr("#fechaInicio", {
            dateFormat: "Y-m-d",
            onChange: function(selectedDates, dateStr) {
                vm.fechaInicio = dateStr;
            }
        });

        flatpickr("#fechaFin", {
            dateFormat: "Y-m-d",
            onChange: function(selectedDates, dateStr) {
                vm.fechaFin = dateStr;
            }
        });
    },

    methods: {
        // Captura el archivo cuando el usuario lo selecciona en el HTML
        handleFileUpload: function(event) {
            this.file = event.target.files[0];
            console.log("Archivo seleccionado:", this.file);
        },

        handleCreateEvent: function() {
            // Limpiamos mensajes previos
            this.errorMsg = '';
            this.isSuccess = false;

            // Validación básica de fechas
            if (this.fechaFin && this.fechaInicio > this.fechaFin) {
                this.errorMsg = "Error: La Fecha de Inicio no puede ser posterior a la Fecha de Fin.";
                return;
            }

            // Iniciamos estado de carga
            this.loading = true;

            // --- PASO CLAVE: Crear el objeto FormData ---
            // Esto permite enviar archivos binarios y texto en la misma petición
            const formData = new FormData();
            formData.append('nombre', this.nombre);
            formData.append('categoria', this.categoria);
            formData.append('fechaInicio', this.fechaInicio);
            formData.append('fechaFin', this.fechaFin || ''); // Enviamos vacío si no hay fecha fin
            formData.append('hora', Number(this.hora));
            formData.append('direccion', this.direccion);
            formData.append('descripcion', this.descripcion);

            // Importante: La clave 'file' debe coincidir con @RequestParam("file") en Java
            formData.append('file', this.file);

            const config = {
                withCredentials: true // Para que mande la cookie de sesión/JSESSIONID
            };

            // Enviamos el FormData mediante POST
            axios.post('/api/eventos', formData, config)
                .then(response => {
                    this.isSuccess = true;
                    this.loading = false;
                    this.errorMsg = "¡Evento creado con éxito! Imagen subida a Cloudinary.";

                    this.resetForm();

                    // Redirigimos después de un tiempo para que el usuario vea el éxito
                    setTimeout(() => {
                        window.location.href = "O_mis_eventos.html";
                    }, 2500);
                })
                .catch(error => {
                    this.loading = false;
                    let msg = "Error al crear evento. Verifique los datos o el tamaño de la imagen.";

                    if (error.response) {
                         if (error.response.status === 401) {
                             msg = "Sesión expirada. Por favor, vuelva a iniciar sesión.";
                         } else if (error.response.data) {
                             msg = "Error del servidor: " + error.response.data;
                         }
                    }

                    console.error("Fallo al crear evento:", error);
                    this.errorMsg = msg;
                    this.isSuccess = false;
                });
        },

        resetForm: function() {
            this.nombre = '';
            this.categoria = '';
            this.fechaInicio = '';
            this.fechaFin = '';
            this.hora = null;
            this.direccion = '';
            this.descripcion = '';
            this.file = null;
            // Limpiamos visualmente el input de archivo
            document.getElementById('file').value = "";
        }
    }
});

app.mount('#createEventApp');