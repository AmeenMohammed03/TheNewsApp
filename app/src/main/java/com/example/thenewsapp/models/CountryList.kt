package com.example.thenewsapp.models

data class CountryList(val name: String, val code: String) {
    object CountryList {
        val countries = listOf(
            CountryList("United Arab Emirates", "ae"),
            CountryList("Argentina", "ar"),
            CountryList("Austria", "at"),
            CountryList("Australia", "au"),
            CountryList("Belgium", "be"),
            CountryList("Bulgaria", "bg"),
            CountryList("Brazil", "br"),
            CountryList("Canada", "ca"),
            CountryList("Switzerland", "ch"),
            CountryList("China", "cn"),
            CountryList("Colombia", "co"),
            CountryList("Cuba", "cu"),
            CountryList("Czech Republic", "cz"),
            CountryList("Germany", "de"),
            CountryList("Egypt", "eg"),
            CountryList("France", "fr"),
            CountryList("United Kingdom", "gb"),
            CountryList("Greece", "gr"),
            CountryList("Hong Kong", "hk"),
            CountryList("Hungary", "hu"),
            CountryList("Indonesia", "id"),
            CountryList("Ireland", "ie"),
            CountryList("Israel", "il"),
            CountryList("India", "in"),
            CountryList("Italy", "it"),
            CountryList("Japan", "jp"),
            CountryList("Republic Of Korea", "kr"),
            CountryList("Lithuania", "lt"),
            CountryList("Latvia", "lv"),
            CountryList("Morocco", "ma"),
            CountryList("Mexico", "mx"),
            CountryList("Malaysia", "my"),
            CountryList("Nigeria", "ng"),
            CountryList("Kingdom Of NetherLands", "nl"),
            CountryList("Norway", "no"),
            CountryList("New Zealand", "nz"),
            CountryList("Philippines", "ph"),
            CountryList("Poland", "pl"),
            CountryList("Portugal", "pt"),
            CountryList("Romania", "ro"),
            CountryList("Serbia", "rs"),
            CountryList("Russian Federation", "ru"),
            CountryList("Saudi Arabia", "sa"),
            CountryList("Sweden", "se"),
            CountryList("Singapore", "sg"),
            CountryList("Slovenia", "si"),
            CountryList("Slovakia", "sk"),
            CountryList("Thailand", "th"),
            CountryList("Turkey", "tr"),
            CountryList("Taiwan", "tw"),
            CountryList("Ukraine", "ua"),
            CountryList("United States Of America", "us"),
            CountryList("Venezuela", "ve"),
            CountryList("South Africa", "za")

        )
    }

    companion object {
        fun getCountryCode(countryName: String): String? {
            return CountryList.countries.find { it.name == countryName }?.code
        }
    }
}