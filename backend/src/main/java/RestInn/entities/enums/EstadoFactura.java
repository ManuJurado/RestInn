package RestInn.entities.enums;

public enum EstadoFactura {
    EN_PROCESO,   // Mientras se agregan consumos
    EMITIDA,      // Finalizada en el check-out
    ANULADA       // Cancelada por error u otro motivo
}
