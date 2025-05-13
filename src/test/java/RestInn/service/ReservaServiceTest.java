package RestInn.service;

import RestInn.dto.reservasDTO.ReservaRequestDTO;
import RestInn.dto.reservasDTO.ReservaResponseDTO;
import RestInn.entities.Reserva;
import RestInn.repositories.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    private ReservaRepository reservaRepository;
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        reservaRepository = mock(ReservaRepository.class);
        reservaService = new ReservaService(reservaRepository);
    }

    @Test
    void obtenerReservas() {
        // Arrange
        Reserva r1 = new Reserva();
        r1.setId(1L);
        r1.setFechaIngreso(LocalDate.of(2025, 5, 1));
        r1.setFechaSalida(LocalDate.of(2025, 5, 5));

        Reserva r2 = new Reserva();
        r2.setId(2L);
        r2.setFechaIngreso(LocalDate.of(2025, 6, 1));
        r2.setFechaSalida(LocalDate.of(2025, 6, 7));

        when(reservaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        // Act
        List<ReservaResponseDTO> result = reservaService.obtenerReservas();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void crearReservaDesdeDto() {
        // Arrange
        ReservaRequestDTO dto = new ReservaRequestDTO(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 10));
        Reserva reservaMock = new Reserva();
        reservaMock.setId(1L);
        reservaMock.setFechaIngreso(dto.getFechaIngreso());
        reservaMock.setFechaSalida(dto.getFechaSalida());

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        // Act
        ReservaResponseDTO response = reservaService.crearReservaDesdeDto(dto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(dto.getFechaIngreso(), response.getFechaIngreso());
        assertEquals(dto.getFechaSalida(), response.getFechaSalida());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void obtenerReservaPorId() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFechaIngreso(LocalDate.of(2025, 5, 1));
        reserva.setFechaSalida(LocalDate.of(2025, 5, 10));

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        ReservaResponseDTO result = reservaService.obtenerReservaPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(reserva.getFechaIngreso(), result.getFechaIngreso());
        assertEquals(reserva.getFechaSalida(), result.getFechaSalida());
    }

    @Test
    void actualizarReservaDesdeDto() {
        // Arrange
        Reserva existing = new Reserva();
        existing.setId(1L);
        existing.setFechaIngreso(LocalDate.of(2025, 5, 1));
        existing.setFechaSalida(LocalDate.of(2025, 5, 5));

        ReservaRequestDTO dto = new ReservaRequestDTO(LocalDate.of(2025, 5, 3), LocalDate.of(2025, 5, 7));
        Reserva updated = new Reserva();
        updated.setId(1L);
        updated.setFechaIngreso(dto.getFechaIngreso());
        updated.setFechaSalida(dto.getFechaSalida());

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(updated);

        // Act
        ReservaResponseDTO response = reservaService.actualizarReservaDesdeDto(1L, dto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(dto.getFechaIngreso(), response.getFechaIngreso());
        assertEquals(dto.getFechaSalida(), response.getFechaSalida());
        verify(reservaRepository).findById(1L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void eliminarReserva() {
        Long id = 1L;

        // Act
        reservaService.eliminarReserva(id);

        // Assert
        verify(reservaRepository).deleteById(id);
    }
}
