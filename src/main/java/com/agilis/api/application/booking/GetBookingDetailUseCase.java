package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.service.ServiceThumbnailRepository;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class GetBookingDetailUseCase {

    // taxa fixa de plataforma — placeholder até o fluxo de pagamento real existir
    private static final BigDecimal PLATFORM_FEE = new BigDecimal("5.00");

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceThumbnailRepository serviceThumbnailRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final StoreUnitRepository storeUnitRepository;
    private final StoreServiceAreaRepository storeServiceAreaRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final UserRepository userRepository;

    public GetBookingDetailUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            ServiceThumbnailRepository serviceThumbnailRepository,
            ProviderProfileRepository providerProfileRepository,
            StoreUnitRepository storeUnitRepository,
            StoreServiceAreaRepository storeServiceAreaRepository,
            StoreMembershipRepository storeMembershipRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository           = bookingRepository;
        this.serviceRepository           = serviceRepository;
        this.serviceThumbnailRepository  = serviceThumbnailRepository;
        this.providerProfileRepository   = providerProfileRepository;
        this.storeUnitRepository         = storeUnitRepository;
        this.storeServiceAreaRepository  = storeServiceAreaRepository;
        this.storeMembershipRepository   = storeMembershipRepository;
        this.userRepository              = userRepository;
    }

    public Output execute(String requesterIdStr, String bookingIdStr) {
        UUID requesterId = UUID.fromString(requesterIdStr);
        UUID bookingId   = UUID.fromString(bookingIdStr);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        boolean isClient = booking.getClientId().equals(requesterId);
        boolean isStoreMember = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .isPresent();

        if (!isClient && !isStoreMember) {
            throw new IllegalStateException("Permission denied to view this booking");
        }

        ProviderProfile store = providerProfileRepository.findById(service.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        String thumbnailUrl = serviceThumbnailRepository.findByServiceId(service.getId())
                .map(t -> t.getUrl())
                .orElse(null);

        String address = resolveAddress(service);

        BigDecimal serviceFee = PLATFORM_FEE;
        BigDecimal total = service.getPrice().add(serviceFee);

        User client = userRepository.findById(booking.getClientId()).orElse(null);

        Actions actions = resolveActions(booking);

        return new Output(
                booking.getId().toString(),
                resolveDisplayStatus(booking),
                service.getTitle(),
                service.getCategory().name(),
                thumbnailUrl,
                service.getPrice(),
                booking.getScheduledAt().toLocalDate(),
                booking.getScheduledAt().toLocalTime(),
                service.getDurationMinutes(),
                address,
                serviceFee,
                total,
                booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null,
                booking.getNotes(),
                store.getStoreName(),
                client != null ? client.getName() : null,
                actions
        );
    }

    private String resolveAddress(Service service) {
        if (service.getUnitId() != null) {
            return storeUnitRepository.findById(service.getUnitId())
                    .map(u -> u.getStreet() + ", " + u.getNumber() + " - " + u.getCity() + " - " + u.getState())
                    .orElse(null);
        }
        return storeServiceAreaRepository.findByStoreId(service.getStoreId())
                .map(StoreServiceArea::getReferenceAddress)
                .orElse(null);
    }

    private String resolveDisplayStatus(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) return "CANCELADO";
        if (booking.getStatus() == BookingStatus.COMPLETED) return "CONCLUIDO";
        if (booking.isInProgress()) return "EM_ANDAMENTO";
        return "AGENDADO";
    }

    private Actions resolveActions(Booking booking) {
        String displayStatus = resolveDisplayStatus(booking);

        boolean canCancel = "AGENDADO".equals(displayStatus);
        boolean canReschedule = "AGENDADO".equals(displayStatus);
        boolean canReportIssue = "CONCLUIDO".equals(displayStatus) && booking.getRefundStatus() == null;

        String refundStatus = booking.getRefundStatus() != null ? booking.getRefundStatus().name() : null;

        return new Actions(canCancel, canReschedule, canReportIssue, refundStatus, booking.getIssueDescription());
    }

    public record Actions(
            boolean canCancel,
            boolean canReschedule,
            boolean canReportIssue,
            String refundStatus,
            String issueDescription
    ) {}

    public record Output(
            String bookingId, String status, String serviceTitle, String category, String thumbnailUrl,
            BigDecimal price, java.time.LocalDate date, java.time.LocalTime time, int durationMinutes,
            String address, BigDecimal serviceFee, BigDecimal total, String paymentMethod, String notes,
            String storeName, String clientName, Actions actions
    ) {}
}