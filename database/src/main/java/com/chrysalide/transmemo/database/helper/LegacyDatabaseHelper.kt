package com.chrysalide.transmemo.database.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class LegacyDatabaseHelper(
    context: Context,
    dbName: String
) : SQLiteOpenHelper(context, dbName, null, 4) {
    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    companion object {
        const val LEGACY_DATABASE_NAME = "TestoMemo.db"

        const val INTAKES_TABLE_NAME = "PRISES"
        const val INTAKES_COLUMN_IDPRISE = "ID_PRISE"
        const val INTAKES_COLUMN_IDPRODUIT = "ID_PRODUIT"
        const val INTAKES_COLUMN_UNITE = "UNITE"
        const val INTAKES_COLUMN_DOSE_PREVUE = "DOSE_PREVUE"
        const val INTAKES_COLUMN_DOSE_REELLE = "DOSE_REELLE"
        const val INTAKES_COLUMN_DATE_PREVUE = "DATE_PREVUE"
        const val INTAKES_COLUMN_DATE_REELLE = "DATE_REELLE"
        const val INTAKES_COLUMN_COTE_PREVU = "COTE_PREVU"
        const val INTAKES_COLUMN_COTE_REEL = "COTE_REEL"

        const val CONTAINERS_TABLE_NAME = "CONTENANTS"
        const val CONTAINERS_COLUMN_IDCONTENANT = "ID_CONTENANT"
        const val CONTAINERS_COLUMN_IDPRODUIT = "ID_PRODUIT"
        const val CONTAINERS_COLUMN_UNITE = "UNITE"
        const val CONTAINERS_COLUMN_CAPACITE_RESTANTE = "CAPACITE_RESTANTE"
        const val CONTAINERS_COLUMN_CAPACITE_UTILISEE = "CAPACITE_UTILISEE"
        const val CONTAINERS_COLUMN_DATE_OUVERTURE = "DATE_OUVERTURE"
        const val CONTAINERS_COLUMN_DATE_PEREMPTION = "DATE_PEREMPTION"
        const val CONTAINERS_COLUMN_ETAT = "ETAT"

        const val PRODUCTS_TABLE_NAME = "PRODUITS"
        const val PRODUCTS_COLUMN_IDPRODUIT = "ID_PRODUIT"
        const val PRODUCTS_COLUMN_NOM_PRODUIT = "NOM_PRODUIT"
        const val PRODUCTS_COLUMN_IDMOLECULE = "ID_MOLECULE"
        const val PRODUCTS_COLUMN_UNITE = "UNITE"
        const val PRODUCTS_COLUMN_DOSE_PRISE = "DOSE_PRISE"
        const val PRODUCTS_COLUMN_CAPACITE = "CAPACITE"
        const val PRODUCTS_COLUMN_JOURS_DLC = "JOURS_DLC"
        const val PRODUCTS_COLUMN_INTERVALLE_PRISES = "INTERVALLE"
        const val PRODUCTS_COLUMN_DELAI_ALERTE = "DELAI_ALERTE"
        const val PRODUCTS_COLUMN_GESTION_COTE = "GESTION_COTE"
        const val PRODUCTS_COLUMN_ETAT = "ETAT"
        const val PRODUCTS_COLUMN_NOTIFICATIONS = "NOTIFICATIONS"

        const val WELLNESS_TABLE_NAME = "BIENETRE"
        const val WELLNESS_COLUMN_DATE = "DATE"
        const val WELLNESS_COLUMN_IDCRITERE = "ID_CRITERE"
        const val WELLNESS_COLUMN_VALEUR = "VALEUR"

        const val NOTES_TABLE_NAME = "NOTES"
        const val NOTES_COLUMN_DATE = "DATE"
        const val NOTES_COLUMN_NOTES = "NOTES"
    }
}
