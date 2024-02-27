package com.example.thenewsapp.models

data class Country(val name: String, val code: String) {
    object CountryList {
        val countries = listOf(
            Country("United Arab Emirates", "ae"),
            Country("Argentina", "ar"),
            Country("Austria", "at"),
            Country("Australia", "au"),
            Country("Belgium", "be"),
            Country("Bulgaria", "bg"),
            Country("Brazil", "br"),
            Country("Canada", "ca"),
            Country("Switzerland", "ch"),
            Country("China", "cn"),
            Country("Colombia", "co"),
            Country("Cuba", "cu"),
            Country("Czech Republic", "cz"),
            Country("Germany", "de"),
            Country("Egypt", "eg"),
            Country("France", "fr"),
            Country("United Kingdom", "gb"),
            Country("Greece", "gr"),
            Country("Hong Kong", "hk"),
            Country("Hungary", "hu"),
            Country("Indonesia", "id"),
            Country("Ireland", "ie"),
            Country("Israel", "il"),
            Country("India", "in"),
            Country("Italy", "it"),
            Country("Japan", "jp"),
            Country("Republic Of Korea", "kr"),
            Country("Lithuania", "lt"),
            Country("Latvia", "lv"),
            Country("Morocco", "ma"),
            Country("Mexico", "mx"),
            Country("Malaysia", "my"),
            Country("Nigeria", "ng"),
            Country("Kingdom Of NetherLands", "nl"),
            Country("Norway", "no"),
            Country("New Zealand", "nz"),
            Country("Philippines", "ph"),
            Country("Poland", "pl"),
            Country("Portugal", "pt"),
            Country("Romania", "ro"),
            Country("Serbia", "rs"),
            Country("Russian Federation", "ru"),
            Country("Saudi Arabia", "sa"),
            Country("Sweden", "se"),
            Country("Singapore", "sg"),
            Country("Slovenia", "si"),
            Country("Slovakia", "sk"),
            Country("Thailand", "th"),
            Country("Turkey", "tr"),
            Country("Taiwan", "tw"),
            Country("Ukraine", "ua"),
            Country("United States Of America", "us"),
            Country("Venezuela", "ve"),
            Country("South Africa", "za")

        )
    }

    companion object {
        fun getCountryCode(countryName: String): String? {
            return CountryList.countries.find { it.name == countryName }?.code
        }
    }
}