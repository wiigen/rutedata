package com.jw.routedata

import com.jw.routedata.domain.Avgang
import com.jw.routedata.domain.Kommune
import com.jw.routedata.domain.RuteStopp
import java.time.LocalDateTime
import kotlin.math.abs

class RuteDataService {

    private val lillehammer = Kommune("Lillehammer", 30_000)
    private val ringsaker = Kommune("Ringsaker kommune", 35_000)
    private val stange = Kommune("Stange kommune", 20_000)
    private val ullensaker = Kommune("Ullensaker kommune", 40_000)
    private val oslo = Kommune("Oslo kommune", 700_000)

    private val stopp0 = RuteStopp(0, "Lillehammer", lillehammer)
    private val stopp1 = RuteStopp(30, "Moelv", ringsaker)
    private val stopp2 = RuteStopp(60, "Brummundal", ringsaker)
    private val stopp3 = RuteStopp(80, "Stange", stange)
    private val stopp4 = RuteStopp(100, "Tangen", stange)
    private val stopp5 = RuteStopp(120, "Oslo Lufthavn", ullensaker)
    private val stopp6 = RuteStopp(150, "Oslo S", oslo)

    // Togrute R10
    private val rute = listOf(stopp0, stopp1, stopp2, stopp3, stopp4, stopp5, stopp6)

    private val avganger = mutableListOf<Avgang>()

    // Oppgave 1
    fun opprettTogAvgang(avgangstid: LocalDateTime) {
        avganger.add(Avgang(avgangstid))
    }

    // Oppgave 2
    fun beregnReisetid(fra: String, til: String): Int {
        val fraStoppested = rute.find { it.stoppested == fra } ?: throw RuntimeException("Fant ikke: $fra")
        val tilStoppested = rute.find { it.stoppested == til } ?: throw RuntimeException("Fant ikke: $til")

        return abs(tilStoppested.reisetid - fraStoppested.reisetid)
    }

    // Oppgave 3
    fun finnAvgangerInnenTidspunkt(tidspunkt: LocalDateTime, stoppested: String): List<Avgang> {
        val stopp = rute.find { it.stoppested == stoppested } ?: throw RuntimeException("Fant ikke: $stoppested")

        return avganger.filter { avgang ->
            val passeringTidspunkt = avgang.avgangstid.plusMinutes(stopp.reisetid.toLong())
            passeringTidspunkt.isBefore(tidspunkt)
        }
    }

    // Oppgave 4
    fun antallPassasjerer(fra: String, til: String, avgangstid: LocalDateTime): Int {
        val reiserute = reiserute(fra, til)

        return reiserute.sumOf { stopp ->
            var prosent = hentReiseStatistikkProsent(avgangstid.plusMinutes(stopp.reisetid.toLong()))

            prosent /= antallStopp(stopp.kommune.navn)

            (stopp.kommune.innbyggere * prosent / 100).toInt()
        }
    }

    // Oppgave 5
    fun regnUtProfitt(fra: String, til: String, avgangstid: LocalDateTime): Int {
        val reiserute = reiserute(fra, til)

        var passasjerer = 0
        var profitt = 0
        for (stopp in reiserute) {
            var prosent = hentReiseStatistikkProsent(avgangstid.plusMinutes(stopp.reisetid.toLong()))

            prosent /= antallStopp(stopp.kommune.navn)

            passasjerer += (stopp.kommune.innbyggere * prosent / 100).toInt()

            profitt += passasjerer * 100
        }
        return profitt
    }

    private fun reiserute(fra: String, til: String): List<RuteStopp> {
        val fraStoppested = rute.find { it.stoppested == fra } ?: throw RuntimeException("Fant ikke: $fra")
        val tilStoppested = rute.find { it.stoppested == til } ?: throw RuntimeException("Fant ikke: $til")

        val indexOfFraStoppested = rute.indexOf(fraStoppested)
        val indexOfTilStoppested = rute.indexOf(tilStoppested)

        return rute.subList(indexOfFraStoppested, indexOfTilStoppested)
    }

    private fun hentReiseStatistikkProsent(tidspunkt: LocalDateTime): Double {
        return when (tidspunkt.hour) {
            in 6..8 -> { // Rush hour
                2.0
            }
            in 9..14 -> {
                0.5
            }
            else -> {
                1.0
            }
        }
    }

    private fun antallStopp(kommuneNavn: String): Int = rute.filter { it.kommune.navn == kommuneNavn }.size

}
