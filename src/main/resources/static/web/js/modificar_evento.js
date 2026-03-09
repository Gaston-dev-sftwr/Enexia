const app = Vue.createApp({
    data() {
        return {
            eventId: null,
            nombre: '',
            categoria: '',
            fechaInicio: '',
            fechaFin: '',
            hora: null,
            direccion: '',
            descripcion: '',
            loadingMsg: "Cargando datos del evento...",
            errorMsg: '',
            isSuccess: false,
        };
    },
    mounted() {
        const urlParams = new URLSearchParams(window.location.search);
        this.eventId = urlParams.get('id');
        if (this.eventId) {
            this.fetchEventData();
        } else {
            this.loadingMsg = "";
            this.errorMsg = "Error: No se especificó ningún ID de evento.";
        }
    },
    methods: {
        
        fetchEventData: function() {
            const vm = this; // Guardamos la referencia a Vue
            
            this.errorMsg = "";
            this.loadingMsg = `Cargando datos del Evento ID: ${vm.eventId}...`;

            axios.get(`/api/eventos/${vm.eventId}`)
                .then(response => {
                    const evento = response.data; 

                    vm.nombre = evento.nombre;
                    vm.categoria = evento.categoria;
                    vm.fechaInicio = evento.fechaInicio; 
                    vm.fechaFin = evento.fechaFin;       
                    vm.hora = evento.hora;
                    vm.direccion = evento.direccion;
                    vm.descripcion = evento.descripcion;
                    
                    vm.loadingMsg = ""; 

                    // === ¡INICIALIZA FLATPICKR AQUÍ! ===
                    
                    flatpickr("#fechaInicio", {
                        dateFormat: "Y-m-d",
                        defaultDate: vm.fechaInicio, // Muestra la fecha cargada
                        onChange: function(selectedDates, dateStr) {
                            vm.fechaInicio = dateStr; // Actualiza el v-model
                        }
                    });

                    flatpickr("#fechaFin", {
                        dateFormat: "Y-m-d",
                        defaultDate: vm.fechaFin, // Muestra la fecha cargada
                        onChange: function(selectedDates, dateStr) {
                            vm.fechaFin = dateStr; // Actualiza el v-model
                        }
                    });
                })
                .catch(error => {
                    console.error("Error al cargar evento:", error);
                    vm.loadingMsg = "";
                    if (error.response && error.response.status === 404) {
                        vm.errorMsg = `Error: Evento con ID ${vm.eventId} no encontrado.`;
                    } else {
                        vm.errorMsg = "Error al cargar los datos del evento.";
                    }
                });
        },

        handleUpdateEvent: function() {
            this.errorMsg = '';
            this.isSuccess = false;

            if (this.fechaFin && this.fechaInicio > this.fechaFin) {
                this.errorMsg = "Error: La Fecha de Inicio no puede ser posterior a la Fecha de Fin.";
                return;
            }

            const eventData = {
                nombre: this.nombre,
                categoria: this.categoria,
                fechaInicio: this.fechaInicio,
                fechaFin: this.fechaFin,
                hora: Number(this.hora),
                direccion: this.direccion,
                descripcion: this.descripcion,
                estado: 1
            };
            
            const config = {
                withCredentials: true 
            };

            axios.put(`/api/eventos/${this.eventId}`, eventData, config)
                .then(response => {
                    this.isSuccess = true;
                    this.errorMsg = "¡Evento actualizado con éxito! Redirigiendo...";
                    
                    setTimeout(() => {
                        window.location.href = "O_mis_eventos.html"; 
                    }, 2000);
                })
                .catch(error => {
                    let msg = "Error al actualizar evento.";
                    if (error.response) {
                        if (error.response.status === 401) {
                            msg = "Sesión expirada. Vuelva a iniciar sesión.";
                             setTimeout(() => { window.location.href = "login.html"; }, 1500);
                        } else if (error.response.status === 403) {
                            msg = "Error: No tienes permiso para modificar este evento.";
                        }
                    }
                    console.error("Fallo al actualizar:", error);
                    this.errorMsg = msg;
                    this.isSuccess = false;
                });
        }
    }
});

app.mount('#modifyEventApp');