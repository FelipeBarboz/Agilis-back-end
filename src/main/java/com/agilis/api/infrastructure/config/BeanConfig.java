package com.agilis.api.infrastructure.config;

import com.agilis.api.application.booking.*;
import com.agilis.api.application.message.*;
import com.agilis.api.application.negotiation.*;
import com.agilis.api.application.provider.RegisterProviderUseCase;
import com.agilis.api.application.review.*;
import com.agilis.api.application.user.RegisterClientUseCase;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.message.MessageRepository;
import com.agilis.api.domain.negotiation.NegotiationRepository;
import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.review.ReviewRepository;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.user.UserRepository;
import com.agilis.api.infrastructure.persistence.booking.BookingJpaRepository;
import com.agilis.api.infrastructure.persistence.booking.BookingRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.client.ClientJpaRepository;
import com.agilis.api.infrastructure.persistence.client.ClientRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.message.MessageJpaRepository;
import com.agilis.api.infrastructure.persistence.message.MessageRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.negotiation.NegotiationJpaRepository;
import com.agilis.api.infrastructure.persistence.negotiation.NegotiationRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.provider.*;
import com.agilis.api.infrastructure.persistence.review.ReviewJpaRepository;
import com.agilis.api.infrastructure.persistence.review.ReviewRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.service.ServiceJpaRepository;
import com.agilis.api.infrastructure.persistence.service.ServiceRepositoryAdapter;
import com.agilis.api.infrastructure.persistence.user.UserJpaRepository;
import com.agilis.api.infrastructure.persistence.user.UserRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

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

    //  USE CASES — USER

    @Bean
    public RegisterClientUseCase registerClientUseCase(
            UserRepository userRepository,
            ClientRepository clientRepository
    ) {
        return new RegisterClientUseCase(userRepository, clientRepository);
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

    //  USE CASES — BOOKING

    @Bean
    public CreateBookingUseCase createBookingUseCase(
            BookingRepository bookingRepository,
            ClientRepository clientRepository,
            ServiceRepository serviceRepository
    ) {
        return new CreateBookingUseCase(bookingRepository, clientRepository, serviceRepository);
    }

    @Bean
    public CancelBookingUseCase cancelBookingUseCase(BookingRepository bookingRepository) {
        return new CancelBookingUseCase(bookingRepository);
    }

    @Bean
    public ConfirmBookingUseCase confirmBookingUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new ConfirmBookingUseCase(bookingRepository, serviceRepository, storeMembershipRepository);
    }

    @Bean
    public CompleteBookingUseCase completeBookingUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new CompleteBookingUseCase(bookingRepository, serviceRepository, storeMembershipRepository);
    }

    @Bean
    public GetBookingUseCase getBookingUseCase(BookingRepository bookingRepository) {
        return new GetBookingUseCase(bookingRepository);
    }

    //  USE CASES — NEGOTIATION

    @Bean
    public CreateNegotiationUseCase createNegotiationUseCase(
            NegotiationRepository negotiationRepository,
            BookingRepository bookingRepository
    ) {
        return new CreateNegotiationUseCase(negotiationRepository, bookingRepository);
    }

    @Bean
    public RespondNegotiationUseCase respondNegotiationUseCase(
            NegotiationRepository negotiationRepository
    ) {
        return new RespondNegotiationUseCase(negotiationRepository);
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
            StoreMembershipRepository storeMembershipRepository
    ) {
        return new SendMessageUseCase(
                messageRepository,
                bookingRepository,
                serviceRepository,
                storeMembershipRepository
        );
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
            BookingRepository bookingRepository
    ) {
        return new CreateReviewUseCase(reviewRepository, bookingRepository);
    }

    @Bean
    public GetReviewUseCase getReviewUseCase(ReviewRepository reviewRepository) {
        return new GetReviewUseCase(reviewRepository);
    }
}