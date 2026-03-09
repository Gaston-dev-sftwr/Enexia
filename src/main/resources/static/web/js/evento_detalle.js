const { createApp } = Vue;

const app = createApp({
    data() {
        return {
            evento: null,
            loading: false,
            errorMessage: ""
        };
    },
    mounted() {
        const params = new URLSearchParams(window.location.search);
        const eventoId = params.get('id');

        if (eventoId) {
            this.fetchEventoById(eventoId);
        } else {
            this.errorMessage = "ID de evento no proporcionado.";
            alert("Error: No se especificó el evento.");
        }
    },
    methods: {
        // --- 1. LLAMADA AL ENDPOINT POR ID (Ruta Relativa) ---
        fetchEventoById(id) {
            this.loading = true;
            // Quitamos 'http://localhost:8081'
            axios.get(`/api/eventos/${id}`)
                .then(res => {
                    this.evento = res.data;
                })
                .catch(err => {
                    console.error("Error cargando detalle:", err);
                    alert("Error al cargar los detalles del evento.");
                })
                .finally(() => this.loading = false);
        },

        // --- 2. INSCRIBIRSE (Ruta Relativa) ---
        inscribirse(eventoId, nombre) {
            // Quitamos 'http://localhost:8081'
            axios.post(`/api/inscribir/${eventoId}`, {}, { withCredentials: true })
                .then(res => {
                    alert(`¡Inscripción exitosa a ${nombre}!`);
                    window.location.href = "mod_historial.html";
                })
                .catch(err => {
                    if (err.response && err.response.status === 409) {
                        alert("Ya estás inscrito en este evento.");
                    } else {
                        alert("Error al procesar la inscripción. Revisa tu sesión.");
                    }
                });
        },

        formatDateTime(fecha, hora) {
            if (!fecha) return 'A confirmar';
            const dateObj = new Date(fecha + 'T00:00:00');
            const dateStr = dateObj.toLocaleDateString('es-ES', { day: 'numeric', month: 'long', year: 'numeric' });
            return `${dateStr} a las ${hora}:00 hs`;
        }
    }
});

app.mount('#app');