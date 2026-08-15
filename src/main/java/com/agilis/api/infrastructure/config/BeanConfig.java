package com.agilis.api.infrastructure.config;

import com.agilis.api.application.booking.*;
import com.agilis.api.application.favorite.AddFavoriteUseCase;
import com.agilis.api.application.favorite.GetFavoritesUseCase;
import com.agilis.api.application.favorite.RemoveFavoriteUseCase;
import com.agilis.api.application.message.*;
import com.agilis.api.application.negotiation.*;
import com.agilis.api.application.notification.ListWebhooksUseCase;
import com.agilis.api.application.notification.RegisterWebhookUseCase;
import com.agilis.api.application.notification.RemoveWebhookUseCase;
import com.agilis.api.application.provider.*;
import com.agilis.api.application.review.*;
import com.agilis.api.application.service.*;
import com.agilis.api.application.user.AddAddressUseCase;
import com.agilis.api.application.user.RegisterClientUseCase;
import com.agilis.api.application.user.UpdateAddressUseCase;
import com.agilis.api.application.user.UpdateUserUseCase;
import com.agilis.api.domain.booking.BookingDelayRepository;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.client.PriorityRebookingRepository;
import com.agilis.api.domain.favorite.FavoriteRepository;
import com.agilis.api.domain.message.MessageRepository;
import com.agilis.api.domain.negotiation.NegotiationRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookSubscriptionRepository;
import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.review.ReviewRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceImageRepository;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.service.ServiceThumbnailRepository;
import com.agilis.api.domain.user.AddressRepository;
import com.agilis.api.domain.user.UserRepository;
import com.agilis.api.infrastructure.notification.WebhookDispatcherAdapter;
import com.agilis.api.infrastructure.persistence.booking.BookingDelayJpaRepository;
import com.agilis.api.infrastructure.persistence.booking.BookingDelayRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.booking.BookingJpaRepository;
import com.agilis.api.infrastructure.persistence.booking.BookingRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.client.ClientJpaRepository;
import com.agilis.api.infrastructure.persistence.client.ClientRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.client.PriorityRebookingJpaRepository;
import com.agilis.api.infrastructure.persistence.client.PriorityRebookingRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.favorite.FavoriteJpaRepository;
import com.agilis.api.infrastructure.persistence.favorite.FavoriteRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.message.MessageJpaRepository;
import com.agilis.api.infrastructure.persistence.message.MessageRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.negotiation.NegotiationJpaRepository;
import com.agilis.api.infrastructure.persistence.negotiation.NegotiationRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.notification.WebhookSubscriptionJpaRepository;
import com.agilis.api.infrastructure.persistence.notification.WebhookSubscriptionRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.provider.*;
import com.agilis.api.infrastructure.persistence.review.ReviewJpaRepository;
import com.agilis.api.infrastructure.persistence.review.ReviewRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.service.*;
import com.agilis.api.infrastructure.persistence.user.AddressJpaRepository;
import com.agilis.api.infrastructure.persistence.user.AddressRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.user.UserJpaRepository;
import com.agilis.api.infrastructure.persistence.user.UserRepositoryAdapter;
import com.agilis.api.infrastructure.security.JwtFilter;
import com.agilis.api.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //  ADAPTERS DE PERSISTÊNCIA

    @Bean
    public UserRepository userRepository(UserJpaRepository jpa) {
        return new UserRepositoryAdapter(jpa);
    }

    @Bean
    public ClientRepository clientRepository(ClientJpaRepository jpa) {
        return new ClientRepositoryAdapter(jpa);
    }

    @Bean
    public ProviderRepository providerRepository(ProviderJpaRepository jpa) {
        return new ProviderRepositoryAdapter(jpa);
    }

    @Bean
    public ProviderProfileRepository providerProfileRepository(ProviderProfileJpaRepository jpa) {
        return new ProviderProfileRepositoryAdapter(jpa);
    }

    @Bean
    public StoreMembershipRepository storeMembershipRepository(StoreMembershipJpaRepository jpa) {
        return new StoreMembershipRepositoryAdapter(jpa);
    }

    @Bean
    public FavoriteRepository favoriteRepository(FavoriteJpaRepository jpa) {
        return new FavoriteRepositoryAdapter(jpa);
    }

    @Bean
    public ServiceRepository serviceRepository(ServiceJpaRepository jpa) {
        return new ServiceRepositoryAdapter(jpa);
    }

    @Bean
    public BookingRepository bookingRepository(BookingJpaRepository jpa) {
        return new BookingRepositoryAdapter(jpa);
    }

    @Bean
    public NegotiationRepository negotiationRepository(NegotiationJpaRepository jpa) {
        return new NegotiationRepositoryAdapter(jpa);
    }

    @Bean
    public MessageRepository messageRepository(MessageJpaRepository jpa) {
        return new MessageRepositoryAdapter(jpa);
    }

    @Bean
    public ReviewRepository reviewRepository(ReviewJpaRepository jpa) {
        return new ReviewRepositoryAdapter(jpa);
    }

    @Bean
    public StoreUnitRepository storeUnitRepository(StoreUnitJpaRepository jpa) {
        return new StoreUnitRepositoryAdapter(jpa);
    }

    @Bean
    public WebhookSubscriptionRepository webhookSubscriptionRepository(WebhookSubscriptionJpaRepository jpa) { return new WebhookSubscriptionRepositoryAdapter(jpa);}

    @Bean
    public PriorityRebookingRepository priorityRebookingRepository(PriorityRebookingJpaRepository jpa) {
        return new PriorityRebookingRepositoryAdapter(jpa);
    }

    @Bean
    public BookingDelayRepository bookingDelayRepository(BookingDelayJpaRepository jpa) {
        return new BookingDelayRepositoryAdapter(jpa);
    }

    @Bean
    public BusinessHoursRepository businessHoursRepository(BusinessHoursJpaRepository jpa) {
        return new BusinessHoursRepositoryAdapter(jpa);
    }

    @Bean
    public EmployeeScheduleRepository employeeScheduleRepository(EmployeeScheduleJpaRepository jpa) {
        return new EmployeeScheduleRepositoryAdapter(jpa);
    }

    @Bean
    public ScheduleSlotRepository scheduleSlotRepository(ScheduleSlotJpaRepository jpa) {
        return new ScheduleSlotRepositoryAdapter(jpa);
    }
    //  USE CASES — USER

    @Bean
    public RegisterClientUseCase registerClientUseCase(
            UserRepository userRepository,
            ClientRepository clientRepository
    ) {
        return new RegisterClientUseCase(userRepository, clientRepository);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepository userRepository) {
        return new UpdateUserUseCase(userRepository);
    }

    @Bean
    public AddressRepository addressRepository(AddressJpaRepository jpa) {
        return new AddressRepositoryAdapter(jpa);
    }

    @Bean
    public AddAddressUseCase addAddressUseCase(AddressRepository addressRepository) {
        return new AddAddressUseCase(addressRepository);
    }

    @Bean
    public UpdateAddressUseCase updateAddressUseCase(AddressRepository addressRepository) {
        return new UpdateAddressUseCase(addressRepository);
    }

    //  USE CASES — PROVIDER

    @Bean
    public RegisterProviderUseCase registerProviderUseCase(
            UserRepository userRepository,
            ProviderRepository providerRepository,
            ProviderProfileRepository providerProfileRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new RegisterProviderUseCase(
                userRepository,
                providerRepository,
                providerProfileRepository,
                storeMembershipRepository
        );
    }

    @Bean
    public SetBusinessHoursUseCase setBusinessHoursUseCase(
            BusinessHoursRepository businessHoursRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new SetBusinessHoursUseCase(businessHoursRepository, storeMembershipRepository);
    }

    @Bean
    public CreateEmployeeScheduleUseCase createEmployeeScheduleUseCase(
            EmployeeScheduleRepository employeeScheduleRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new CreateEmployeeScheduleUseCase(employeeScheduleRepository, storeMembershipRepository);
    }

    @Bean
    public AddScheduleSlotUseCase addScheduleSlotUseCase(
            ScheduleSlotRepository scheduleSlotRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new AddScheduleSlotUseCase(scheduleSlotRepository, employeeScheduleRepository, storeMembershipRepository);
    }

    @Bean
    public RemoveScheduleSlotUseCase removeScheduleSlotUseCase(
            ScheduleSlotRepository scheduleSlotRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new RemoveScheduleSlotUseCase(scheduleSlotRepository, employeeScheduleRepository, storeMembershipRepository);
    }

    //  USE CASES — BOOKING

    @Bean
    public CreateBookingUseCase createBookingUseCase(
            BookingRepository bookingRepository,
            ClientRepository clientRepository,
            ServiceRepository serviceRepository,
            PriorityRebookingRepository priorityRebookingRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new CreateBookingUseCase(bookingRepository, clientRepository, serviceRepository, priorityRebookingRepository, webhookDispatcher);
    }

    @Bean
    public CancelBookingUseCase cancelBookingUseCase(BookingRepository bookingRepository, WebhookDispatcher webhookDispatcher, ServiceRepository service) {
        return new CancelBookingUseCase(bookingRepository, webhookDispatcher, service);
    }

    @Bean
    public ConfirmBookingUseCase confirmBookingUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new ConfirmBookingUseCase(bookingRepository, serviceRepository, storeMembershipRepository, webhookDispatcher);
    }

    @Bean
    public CompleteBookingUseCase completeBookingUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new CompleteBookingUseCase(bookingRepository, serviceRepository, storeMembershipRepository, webhookDispatcher);
    }

    @Bean
    public GetBookingUseCase getBookingUseCase(BookingRepository bookingRepository) {
        return new GetBookingUseCase(bookingRepository);
    }

    @Bean
    public GetStoreBookingsUseCase getStoreBookingsUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new GetStoreBookingsUseCase(bookingRepository, serviceRepository, storeMembershipRepository);
    }

    @Bean
    public DeclareDelayUseCase declareDelayUseCase(
            BookingRepository bookingRepository,
            BookingDelayRepository bookingDelayRepository,
            StoreMembershipRepository storeMembershipRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new DeclareDelayUseCase(bookingRepository, bookingDelayRepository, storeMembershipRepository, webhookDispatcher);
    }

    @Bean
    public GetPendingDelaysUseCase getPendingDelaysUseCase(BookingDelayRepository bookingDelayRepository) {
        return new GetPendingDelaysUseCase(bookingDelayRepository);
    }

    @Bean
    public RespondToDelayUseCase respondToDelayUseCase(
            BookingDelayRepository bookingDelayRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            PriorityRebookingRepository priorityRebookingRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new RespondToDelayUseCase(bookingDelayRepository, bookingRepository, serviceRepository, priorityRebookingRepository, webhookDispatcher);
    }

    //  USE CASES — NEGOTIATION

    @Bean
    public CreateNegotiationUseCase createNegotiationUseCase(
            NegotiationRepository negotiationRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new CreateNegotiationUseCase(negotiationRepository, bookingRepository, serviceRepository, webhookDispatcher);
    }

    @Bean
    public RespondNegotiationUseCase respondNegotiationUseCase(
            NegotiationRepository negotiationRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new RespondNegotiationUseCase(negotiationRepository, bookingRepository, serviceRepository, webhookDispatcher);
    }

    @Bean
    public GetNegotiationUseCase getNegotiationUseCase(
            NegotiationRepository negotiationRepository
    ) {
        return new GetNegotiationUseCase(negotiationRepository);
    }

    //  USE CASES — MESSAGE

    @Bean
    public SendMessageUseCase sendMessageUseCase(
            MessageRepository messageRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new SendMessageUseCase(messageRepository, bookingRepository, serviceRepository, storeMembershipRepository, webhookDispatcher);
    }

    @Bean
    public GetMessagesUseCase getMessagesUseCase(
            MessageRepository messageRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new GetMessagesUseCase(
                messageRepository,
                bookingRepository,
                serviceRepository,
                storeMembershipRepository
        );
    }

    //  USE CASES — REVIEW

    @Bean
    public CreateReviewUseCase createReviewUseCase(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new CreateReviewUseCase(reviewRepository, bookingRepository, serviceRepository, webhookDispatcher);
    }

    @Bean
    public GetReviewUseCase getReviewUseCase(ReviewRepository reviewRepository) {
        return new GetReviewUseCase(reviewRepository);
    }

    //  USE CASES — PROVIDER

    @Bean
    public GetMyStoresUseCase getMyStoresUseCase(
            StoreMembershipRepository storeMembershipRepository,
            ProviderProfileRepository providerProfileRepository
    ) {
        return new GetMyStoresUseCase(storeMembershipRepository, providerProfileRepository);
    }

    @Bean
    public UpdateProviderProfileUseCase updateProviderProfileUseCase(
            ProviderProfileRepository providerProfileRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new UpdateProviderProfileUseCase(providerProfileRepository, storeMembershipRepository);
    }

    @Bean
    public InviteMemberUseCase inviteMemberUseCase(
            StoreMembershipRepository storeMembershipRepository,
            ProviderRepository providerRepository,
            UserRepository userRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new InviteMemberUseCase(storeMembershipRepository, providerRepository, userRepository, webhookDispatcher);
    }

    @Bean
    public RemoveMemberUseCase removeMemberUseCase(
            StoreMembershipRepository storeMembershipRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        return new RemoveMemberUseCase(storeMembershipRepository, webhookDispatcher);
    }

    @Bean
    public GetAvailableSlotsUseCase getAvailableSlotsUseCase(
            ServiceRepository serviceRepository,
            ProviderProfileRepository providerProfileRepository,
            BusinessHoursRepository businessHoursRepository,
            StoreMembershipRepository storeMembershipRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            ScheduleSlotRepository scheduleSlotRepository,
            BookingRepository bookingRepository
    ) {
        return new GetAvailableSlotsUseCase(
                serviceRepository, providerProfileRepository, businessHoursRepository,
                storeMembershipRepository, employeeScheduleRepository, scheduleSlotRepository, bookingRepository
        );
    }

    @Bean
    public ToggleEmployeeSelectionUseCase toggleEmployeeSelectionUseCase(
            ProviderProfileRepository providerProfileRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new ToggleEmployeeSelectionUseCase(providerProfileRepository, storeMembershipRepository);
    }

    //  USE CASES — SERVICE
    @Bean
    public CreateServiceUseCase createServiceUseCase(
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new CreateServiceUseCase(serviceRepository, storeMembershipRepository);
    }

    @Bean
    public UpdateServiceUseCase updateServiceUseCase(
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new UpdateServiceUseCase(serviceRepository, storeMembershipRepository);
    }

    @Bean
    public DeleteServiceUseCase deleteServiceUseCase(
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new DeleteServiceUseCase(serviceRepository, storeMembershipRepository);
    }

    @Bean
    public ServiceImageRepository serviceImageRepository(ServiceImageJpaRepository jpa) {
        return new ServiceImageRepositoryAdapter(jpa);
    }

    @Bean
    public ServiceThumbnailRepository serviceThumbnailRepository(ServiceThumbnailJpaRepository jpa) {
        return new ServiceThumbnailRepositoryAdapter(jpa);
    }

    @Bean
    public AddServiceImageUseCase addServiceImageUseCase(
            ServiceImageRepository serviceImageRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new AddServiceImageUseCase(serviceImageRepository, serviceRepository, storeMembershipRepository);
    }

    @Bean
    public RemoveServiceImageUseCase removeServiceImageUseCase(
            ServiceImageRepository serviceImageRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new RemoveServiceImageUseCase(serviceImageRepository, serviceRepository, storeMembershipRepository);
    }

    @Bean
    public UpdateThumbnailUseCase updateThumbnailUseCase(
            ServiceThumbnailRepository serviceThumbnailRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new UpdateThumbnailUseCase(serviceThumbnailRepository, serviceRepository, storeMembershipRepository);
    }

    //  USE CASES — FAVORITE

    @Bean
    public AddFavoriteUseCase addFavoriteUseCase(
            FavoriteRepository favoriteRepository,
            ServiceRepository serviceRepository
    ) {
        return new AddFavoriteUseCase(favoriteRepository, serviceRepository);
    }

    @Bean
    public RemoveFavoriteUseCase removeFavoriteUseCase(FavoriteRepository favoriteRepository) {
        return new RemoveFavoriteUseCase(favoriteRepository);
    }

    @Bean
    public GetFavoritesUseCase getFavoritesUseCase(
            FavoriteRepository favoriteRepository,
            ServiceRepository serviceRepository
    ) {
        return new GetFavoritesUseCase(favoriteRepository, serviceRepository);
    }

    // USE CASES - STORE UNIT

    @Bean
    public CreateStoreUnitUseCase createStoreUnitUseCase(
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new CreateStoreUnitUseCase(storeUnitRepository, storeMembershipRepository);
    }

    @Bean
    public UpdateStoreUnitUseCase updateStoreUnitUseCase(
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new UpdateStoreUnitUseCase(storeUnitRepository, storeMembershipRepository);
    }

    @Bean
    public DeleteStoreUnitUseCase deleteStoreUnitUseCase(
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new DeleteStoreUnitUseCase(storeUnitRepository, storeMembershipRepository);
    }

    @Bean
    public WebhookDispatcher webhookDispatcher(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        return new WebhookDispatcherAdapter(webhookSubscriptionRepository, restTemplate, objectMapper);
    }

    @Bean
    public RegisterWebhookUseCase registerWebhookUseCase(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new RegisterWebhookUseCase(webhookSubscriptionRepository, storeMembershipRepository);
    }

    @Bean
    public ListWebhooksUseCase listWebhooksUseCase(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new ListWebhooksUseCase(webhookSubscriptionRepository, storeMembershipRepository);
    }

    @Bean
    public RemoveWebhookUseCase removeWebhookUseCase(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new RemoveWebhookUseCase(webhookSubscriptionRepository, storeMembershipRepository);
    }

    // Jwt
    @Bean
    public JwtFilter jwtFilter(JwtService jwtService) {
        return new JwtFilter(jwtService);
    }
}