package roomescape.reservation.dao;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.Time;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationRepositoryTest {

    private static final Member MEMBER = new Member(1L, "범블비", "aa@email.com", "1111");
    private static final Time TIME = new Time(1L, LocalTime.of(12, 0));
    private static final Theme THEME = new Theme(1L, "Harry Potter", "해리포터와 도비", "thumbnail.jpg");
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        entityManager.merge(MEMBER);
        entityManager.merge(TIME);
        entityManager.merge(THEME);
        entityManager.flush();
    }

    @Test
    @DisplayName("데이터를 정상적으로 저장한다.")
    void saveReservation() {
        Reservation reservation = new Reservation(1L, MEMBER, LocalDate.MAX, TIME, THEME);

        reservationRepository.save(reservation);

        List<Reservation> expected = reservationRepository.findAll();
        assertThat(reservation).isEqualTo(expected.iterator()
                .next());
    }

    @Test
    @DisplayName("데이터를 정상적으로 조회한다.")
    void getReservations() {
        // Given
        Reservation reservation = new Reservation(1L, MEMBER, LocalDate.MAX, TIME, THEME);

        entityManager.merge(reservation);

        // When
        List<Reservation> reservations = reservationRepository.findAllByOrderByDateAsc();

        // Then
        assertThat(reservations.size())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("데이터를 정상적으로 삭제한다.")
    void deleteReservations() {
        Reservation reservation = new Reservation(1L, MEMBER, LocalDate.MAX, TIME, THEME);

        Reservation expectedReservation = entityManager.merge(reservation);

        reservationRepository.deleteById(expectedReservation.getId());

        assertThat(reservationRepository.findById(expectedReservation.getId()))
                .isEmpty();
    }

    @Test
    @DisplayName("존재하는 예약시간인지 확인한다.")
    void countReservationTime() {
        Reservation reservation = new Reservation(1L, MEMBER, LocalDate.MAX, TIME, THEME);
        Reservation expectedReservation = entityManager.merge(reservation);

        assertThat(reservationRepository.countReservationsByTime_Id(expectedReservation.getTimeId()))
                .isEqualTo(1);
    }

}
