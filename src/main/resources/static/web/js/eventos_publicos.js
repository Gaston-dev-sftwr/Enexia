const app = Vue.createApp({
    data() {
        return {
            events: [],
            loadingMsg: "Buscando eventos disponibles...",
            errorMessage: "",
        };
    },
    mounted() {
        this.fetchPublicEvents();
    },
    methods: {
        fetchPublicEvents: function() {
            this.errorMessage = "";
            this.loadingMsg = "Buscando eventos disponibles...";

            // Llamada al endpoint público
            axios.get('/api/eventos/publico')
                .then(response => {
                    this.loadingMsg = "";
                    
                    // --- ¡IMPORTANTE! ---
                    // Filtramos solo los eventos que están activos (estado == 1)
                    const activeEvents = response.data.filter(evento => evento.estado === 1);

                    if (activeEvents.length === 0) {
                        this.errorMessage = "No hay eventos disponibles en este momento.";
                        this.events = [];
                    } else {
                        this.events = activeEvents;
                    }
                })
                .catch(error => {
                    this.loadingMsg = "";
                    this.errorMessage = "Error al conectar con el servidor. Intente más tarde.";
                    console.error("Error al cargar eventos públicos:", error);
                });
        },
        
        // --- TU FUNCIÓN DE FORMATO (ahora es un método de Vue) ---
        formatDateTime: function(fecha, hora) {
            if (!fecha) return 'Fecha no definida';
            
            try {
                const dateObj = new Date(fecha + 'T00:00:00'); 
                const datePart = dateObj.toLocaleDateString('es-ES', { 
                    year: 'numeric', month: 'long', day: 'numeric' 
                });
                const timePart = hora !== undefined ? `${String(hora).padStart(2, '0')}:00` : '';

                return `${datePart} ${timePart ? ' a las ' + timePart : ''}`;

            } catch (e) {
                console.error("Error al formatear fecha:", e);
                return 'Fecha inválida';
            }
        }
    }
});

app.mount('#publicEventsApp');