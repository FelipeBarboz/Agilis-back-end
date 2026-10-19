package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.favorite.FavoriteRepository;
import com.agilis.api.domain.provider.ProviderProfileRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.service.ServiceThumbnailRepository;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class GetBookingHistoryUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceThumbnailRepository serviceThumbnailRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public GetBookingHistoryUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            ServiceThumbnailRepository serviceThumbnailRepository,
            ProviderProfileRepository providerProfileRepository,
            UserRepository userRepository,
            FavoriteRepository favoriteRepository
    ) {
        this.bookingRepository            = bookingRepository;
        this.serviceRepository            = serviceRepository;
        this.serviceThumbnailRepository   = serviceThumbnailRepository;
        this.providerProfileRepository    = providerProfileRepository;
        this.userRepository               = userRepository;
        this.favoriteRepository           = favoriteRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());

        List<Booking> allBookings = input.role() == Role.CLIENT
                ? bookingRepository.findAllByClientId(requesterId)
                : bookingRepository.findAllByEmployeeId(requesterId);

        Counts counts = countByStatus(allBookings);

        List<Booking> filtered = allBookings.stream()
                .filter(b -> input.statusFilter() == null || input.statusFilter() == DisplayStatus.TODOS
                        || resolveDisplayStatus(b) == input.statusFilter())
                .sorted((a, b) -> b.getScheduledAt().compareTo(a.getScheduledAt())) // mais recente primeiro
                .toList();

        List<Item> items = filtered.stream()
                .map(b -> toItem(b, requesterId, input.role()))
                .toList();

        return new Output(items, counts);
    }

    private Item toItem(Booking booking, UUID requesterId, Role role) {
        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        String thumbnailUrl = serviceThumbnailRepository.findByServiceId(service.getId())
                .map(t -> t.getUrl())
                .orElse(null);

        String otherPartyName;
        if (role == Role.CLIENT) {
            otherPartyName = providerProfileRepository.findById(service.getStoreId())
                    .map(p -> p.getStoreName())
                    .orElse("Prestador");
        } else {
            User client = userRepository.findById(booking.getClientId()).orElse(null);
            otherPartyName = client != null ? client.getName() : "Cliente";
        }

        boolean isFavorited = role == Role.CLIENT &&
                favoriteRepository.existsByUserIdAndServiceId(requesterId, service.getId());

        return new Item(
                booking.getId().toString(),
                service.getTitle(),
                thumbnailUrl,
                resolveDisplayStatus(booking).name(),
                otherPartyName,
                service.getCategory().name(),
                booking.getScheduledAt().toLocalDate(),
                booking.getScheduledAt().toLocalTime(),
                service.getPrice(),
                isFavorited
        );
    }

    private DisplayStatus resolveDisplayStatus(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) return DisplayStatus.CANCELADO;
        if (booking.getStatus() == BookingStatus.COMPLETED) return DisplayStatus.CONCLUIDO;
        if (booking.isInProgress()) return DisplayStatus.EM_ANDAMENTO;
        return DisplayStatus.AGENDADO;
    }

    private Counts countByStatus(List<Booking> bookings) {
        long agendado    = bookings.stream().filter(b -> resolveDisplayStatus(b) == DisplayStatus.AGENDADO).count();
        long emAndamento = bookings.stream().filter(b -> resolveDisplayStatus(b) == DisplayStatus.EM_ANDAMENTO).count();
        long concluido   = bookings.stream().filter(b -> resolveDisplayStatus(b) == DisplayStatus.CONCLUIDO).count();
        long cancelado   = bookings.stream().filter(b -> resolveDisplayStatus(b) == DisplayStatus.CANCELADO).count();
        return new Counts(bookings.size(), agendado, emAndamento, concluido, cancelado);
    }

    public enum Role { CLIENT, PROVIDER }
    public enum DisplayStatus { TODOS, AGENDADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO }

    public record Input(String requesterId, Role role, DisplayStatus statusFilter) {}

    public record Item(
            String bookingId, String serviceTitle, String thumbnailUrl, String status,
            String otherPartyName, String category, LocalDate date, LocalTime time,
            BigDecimal price, boolean isFavorited
    ) {}

    public record Counts(long total, long agendado, long emAndamento, long concluido, long cancelado) {}

    public record Output(List<Item> items, Counts counts) {}
}