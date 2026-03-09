const app = Vue.createApp({
    data() {
        return {
            events: [],
            loadingMsg: "Cargando tus eventos...",
            errorMessage: "",
        };
    },
    mounted() {
        this.fetchMyEvents();
    },
    methods: {
        fetchMyEvents: function() {
            this.errorMessage = "";
            this.loadingMsg = "Cargando tus eventos...";

            axios.get('/api/eventos/mis-eventos', { withCredentials: true })
                .then(response => {
                    this.loadingMsg = "";
                    
                    // CORRECCIÓN: Tu backend envía 'Integer' 1, no 'Boolean' true.
                    const activeEvents = response.data.filter(e => e.estado === 1);

                    if (activeEvents.length === 0) {
                        this.errorMessage = "No hay eventos creados. ¡Empieza a crear uno!";
                    } else {
                        this.events = activeEvents;
                    }
                })
                .catch(error => {
                    this.loadingMsg = "";
                    console.error("Error al cargar eventos propios:", error.response);

                    if (error.response && error.response.status === 401) {
                        this.errorMessage = "Sesión expirada. Redirigiendo a Login.";
                        setTimeout(() => { window.location.href = "login.html"; }, 1500);
                    } else {
                        this.errorMessage = "Error de conexión al servidor.";
                    }
                });
        },
        
        // --- CU6: Borrado Lógico (Eliminar) ---
        handleDelete: function(eventId) {
            
            // Reemplazo del 'confirm()' nativo por SweetAlert2
            Swal.fire({
                title: '¿Estás seguro?',
                text: `¿Quieres eliminar lógicamente el evento ID ${eventId}? Esta acción es irreversible.`,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#5c7a95', // Tu color de acento
                cancelButtonColor: '#ff4d4d', // Un color de peligro
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar',
                background: '#28282b', // Fondo oscuro (combina con tu CSS)
                color: '#f2f2f7'      // Texto claro (combina con tu CSS)
            }).then((result) => {
                
                // Si el usuario hace clic en "Sí, eliminar"
                if (result.isConfirmed) {
                    
                    // Tu lógica de Axios que ya tenías
                    axios.delete(`/api/eventos/${eventId}`, { withCredentials: true })
                        .then(() => {
                            // Pop-up de éxito
                            Swal.fire({
                                title: '¡Eliminado!',
                                text: `Evento ID ${eventId} desactivado con éxito.`,
                                icon: 'success',
                                background: '#28282b',
                                color: '#f2f2f7',
                                confirmButtonColor: '#5c7a95',
                            });

                            // Refrescar la lista en el frontend
                            this.events = this.events.filter(e => e.id !== eventId);
                            this.checkListEmpty();
                        })
                        .catch(error => {
                            console.error("Error al eliminar:", error.response);
                            const msg = error.response.status === 403 
                                ? "ERROR: No tienes permiso para eliminar este evento."
                                : "Fallo al eliminar evento.";
                            
                            // Pop-up de error
                            Swal.fire({
                                title: 'Error',
                                text: msg,
                                icon: 'error',
                                background: '#28282b',
                                color: '#f2f2f7',
                                confirmButtonColor: '#5c7a95',
                            });
                        });
                }
            });
        },

        checkListEmpty: function() {
            if (this.events.length === 0) {
                this.errorMessage = "No hay eventos creados. ¡Empieza a crear uno!";
            }
        },

        goToEdit: function(eventId) {
            // Navegar a la página de Modificar Evento
            window.location.href = `O_modificar_evento.html?id=${eventId}`;
        }
    }
});

app.mount('#misEventosApp');