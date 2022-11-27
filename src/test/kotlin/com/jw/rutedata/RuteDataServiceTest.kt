package com.jw.rutedata

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class RuteDataServiceTest {

    @Test
    fun `Reise fra Stange til Oslo S skal ta 70 minutter`() {
        val reisetid = RuteDataService().beregnReisetid("Stange", "Oslo S")
        assertEquals(70, reisetid)
    }

    @Test
    fun `Reise fra Tangen til Stange skal ta 20 minutes`() {
        val reisetid = RuteDataService().beregnReisetid("Tangen", "Stange")
        assertEquals(20, reisetid)
    }

    @Test
    fun `Finn avganger innen tidspunkt for gitt stoppested skal returnere 2 avganger`() {
        val ruteDataService = RuteDataService()
        val tidspunkt = LocalDateTime.of(2022, 11, 18, 10, 0)

        ruteDataService.opprettTogAvgang(tidspunkt.minusHours(2))
        ruteDataService.opprettTogAvgang(tidspunkt.plusHours(1))
        ruteDataService.opprettTogAvgang(tidspunkt.minusHours(3))

        val avganger = ruteDataService.finnAvgangerInnenTidspunkt(tidspunkt, "Tangen")
        assertEquals(2, avganger.size)
    }

    @Test
    fun `Antall passajerer fra Lillehammer til Oslo S er lik 2025 ved 05 avgang`() {
        val avgangstid = LocalDateTime.of(2020, 11, 18, 5, 0)

        val antallPassasjerer = RuteDataService().antallPassasjerer("Lillehammer", "Oslo S", avgangstid)
        assertEquals(2025, antallPassasjerer)
    }

    @Test
    fun `Togoperator skal tjene 262500 kr for 05 avgang mellom Lillehammer og Tangen`() {
        val avgangstid = LocalDateTime.of(2020, 11, 18, 5, 0)

        val profitt = RuteDataService().regnUtProfitt("Lillehammer", "Tangen", avgangstid)
        assertEquals(262500, profitt)
    }

}