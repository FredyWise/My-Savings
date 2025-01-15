package com.fredy.currency.data.mappers


import com.fredy.currency.data.currencyModels.currencyDTO.Rates
import com.fredy.domain.model.Rate


fun Rates.toList(): List<Rate> {
    return listOfNotNull(
        Rate("AED", this.AED.toDouble()),
        Rate("AFN", this.AFN.toDouble()),
        Rate("ALL", this.ALL.toDouble()),
        Rate("AMD", this.AMD.toDouble()),
        Rate("ANG", this.ANG.toDouble()),
        Rate("AOA", this.AOA.toDouble()),
        Rate("ARS", this.ARS.toDouble()),
        Rate("AUD", this.AUD.toDouble()),
        Rate("AWG", this.AWG.toDouble()),
        Rate("AZN", this.AZN.toDouble()),
        Rate("BAM", this.BAM.toDouble()),
        Rate("BBD", this.BBD.toDouble()),
        Rate("BDT", this.BDT.toDouble()),
        Rate("BGN", this.BGN.toDouble()),
        Rate("BHD", this.BHD.toDouble()),
        Rate("BIF", this.BIF.toDouble()),
        Rate("BMD", this.BMD.toDouble()),
        Rate("BND", this.BND.toDouble()),
        Rate("BOB", this.BOB.toDouble()),
        Rate("BRL", this.BRL.toDouble()),
        Rate("BSD", this.BSD.toDouble()),
        Rate("BTC", this.BTC.toDouble()),
        Rate("BTN", this.BTN.toDouble()),
        Rate("BWP", this.BWP.toDouble()),
        Rate("BYN", this.BYN.toDouble()),
        Rate("BYR", this.BYR.toDouble()),
        Rate("BZD", this.BZD.toDouble()),
        Rate("CAD", this.CAD.toDouble()),
        Rate("CDF", this.CDF.toDouble()),
        Rate("CHF", this.CHF.toDouble()),
        Rate("CLF", this.CLF.toDouble()),
        Rate("CLP", this.CLP.toDouble()),
        Rate("CNY", this.CNY.toDouble()),
        Rate("COP", this.COP.toDouble()),
        Rate("CRC", this.CRC.toDouble()),
        Rate("CUC", this.CUC.toDouble()),
        Rate("CUP", this.CUP.toDouble()),
        Rate("CVE", this.CVE.toDouble()),
        Rate("CZK", this.CZK.toDouble()),
        Rate("DJF", this.DJF.toDouble()),
        Rate("DKK", this.DKK.toDouble()),
        Rate("DOP", this.DOP.toDouble()),
        Rate("DZD", this.DZD.toDouble()),
        Rate("EGP", this.EGP.toDouble()),
        Rate("ERN", this.ERN.toDouble()),
        Rate("ETB", this.ETB.toDouble()),
        Rate("EUR", this.EUR.toDouble()),
        Rate("FJD", this.FJD.toDouble()),
        Rate("FKP", this.FKP.toDouble()),
        Rate("GBP", this.GBP.toDouble()),
        Rate("GEL", this.GEL.toDouble()),
        Rate("GGP", this.GGP.toDouble()),
        Rate("GHS", this.GHS.toDouble()),
        Rate("GIP", this.GIP.toDouble()),
        Rate("GMD", this.GMD.toDouble()),
        Rate("GNF", this.GNF.toDouble()),
        Rate("GTQ", this.GTQ.toDouble()),
        Rate("GYD", this.GYD.toDouble()),
        Rate("HKD", this.HKD.toDouble()),
        Rate("HNL", this.HNL.toDouble()),
        Rate("HRK", this.HRK.toDouble()),
        Rate("HTG", this.HTG.toDouble()),
        Rate("HUF", this.HUF.toDouble()),
        Rate("IDR", this.IDR.toDouble()),
        Rate("ILS", this.ILS.toDouble()),
        Rate("IMP", this.IMP.toDouble()),
        Rate("INR", this.INR.toDouble()),
        Rate("IQD", this.IQD.toDouble()),
        Rate("IRR", this.IRR.toDouble()),
        Rate("ISK", this.ISK.toDouble()),
        Rate("JEP", this.JEP.toDouble()),
        Rate("JMD", this.JMD.toDouble()),
        Rate("JOD", this.JOD.toDouble()),
        Rate("JPY", this.JPY.toDouble()),
        Rate("KES", this.KES.toDouble()),
        Rate("KGS", this.KGS.toDouble()),
        Rate("KHR", this.KHR.toDouble()),
        Rate("KMF", this.KMF.toDouble()),
        Rate("KPW", this.KPW.toDouble()),
        Rate("KRW", this.KRW.toDouble()),
        Rate("KWD", this.KWD.toDouble()),
        Rate("KYD", this.KYD.toDouble()),
        Rate("KZT", this.KZT.toDouble()),
        Rate("LAK", this.LAK.toDouble()),
        Rate("LBP", this.LBP.toDouble()),
        Rate("LKR", this.LKR.toDouble()),
        Rate("LRD", this.LRD.toDouble()),
        Rate("LSL", this.LSL.toDouble()),
        Rate("LTL", this.LTL.toDouble()),
        Rate("LVL", this.LVL.toDouble()),
        Rate("LYD", this.LYD.toDouble()),
        Rate("MAD", this.MAD.toDouble()),
        Rate("MDL", this.MDL.toDouble()),
        Rate("MGA", this.MGA.toDouble()),
        Rate("MKD", this.MKD.toDouble()),
        Rate("MMK", this.MMK.toDouble()),
        Rate("MNT", this.MNT.toDouble()),
        Rate("MOP", this.MOP.toDouble()),
        Rate("MRO", this.MRO.toDouble()),
        Rate("MUR", this.MUR.toDouble()),
        Rate("MVR", this.MVR.toDouble()),
        Rate("MWK", this.MWK.toDouble()),
        Rate("MXN", this.MXN.toDouble()),
        Rate("MYR", this.MYR.toDouble()),
        Rate("MZN", this.MZN.toDouble()),
        Rate("NAD", this.NAD.toDouble()),
        Rate("NGN", this.NGN.toDouble()),
        Rate("NIO", this.NIO.toDouble()),
        Rate("NOK", this.NOK.toDouble()),
        Rate("NPR", this.NPR.toDouble()),
        Rate("NZD", this.NZD.toDouble()),
        Rate("OMR", this.OMR.toDouble()),
        Rate("PAB", this.PAB.toDouble()),
        Rate("PEN", this.PEN.toDouble()),
        Rate("PGK", this.PGK.toDouble()),
        Rate("PHP", this.PHP.toDouble()),
        Rate("PKR", this.PKR.toDouble()),
        Rate("PLN", this.PLN.toDouble()),
        Rate("PYG", this.PYG.toDouble()),
        Rate("QAR", this.QAR.toDouble()),
        Rate("RON", this.RON.toDouble()),
        Rate("RSD", this.RSD.toDouble()),
        Rate("RUB", this.RUB.toDouble()),
        Rate("RWF", this.RWF.toDouble()),
        Rate("SAR", this.SAR.toDouble()),
        Rate("SBD", this.SBD.toDouble()),
        Rate("SCR", this.SCR.toDouble()),
        Rate("SDG", this.SDG.toDouble()),
        Rate("SEK", this.SEK.toDouble()),
        Rate("SGD", this.SGD.toDouble()),
        Rate("SHP", this.SHP.toDouble()),
        Rate("SLE", this.SLE.toDouble()),
        Rate("SLL", this.SLL.toDouble()),
        Rate("SOS", this.SOS.toDouble()),
        Rate("SRD", this.SRD.toDouble()),
        Rate("STD", this.STD.toDouble()),
        Rate("SYP", this.SYP.toDouble()),
        Rate("SZL", this.SZL.toDouble()),
        Rate("THB", this.THB.toDouble()),
        Rate("TJS", this.TJS.toDouble()),
        Rate("TMT", this.TMT.toDouble()),
        Rate("TND", this.TND.toDouble()),
        Rate("TOP", this.TOP.toDouble()),
        Rate("TRY", this.TRY.toDouble()),
        Rate("TTD", this.TTD.toDouble()),
        Rate("TWD", this.TWD.toDouble()),
        Rate("TZS", this.TZS.toDouble()),
        Rate("UAH", this.UAH.toDouble()),
        Rate("UGX", this.UGX.toDouble()),
        Rate("USD", this.USD.toDouble()),
        Rate("UYU", this.UYU.toDouble()),
        Rate("UZS", this.UZS.toDouble()),
        Rate("VEF", this.VEF.toDouble()),
        Rate("VES", this.VES.toDouble()),
        Rate("VND", this.VND.toDouble()),
        Rate("VUV", this.VUV.toDouble()),
        Rate("WST", this.WST.toDouble()),
        Rate("XAF", this.XAF.toDouble()),
        Rate("XAG", this.XAG.toDouble()),
        Rate("XAU", this.XAU.toDouble()),
        Rate("XCD", this.XCD.toDouble()),
        Rate("XDR", this.XDR.toDouble()),
        Rate("XOF", this.XOF.toDouble()),
        Rate("XPF", this.XPF.toDouble()),
        Rate("YER", this.YER.toDouble()),
        Rate("ZAR", this.ZAR.toDouble()),
        Rate("ZMK", this.ZMK.toDouble()),
        Rate("ZMW", this.ZMW.toDouble()),
        Rate("ZWL", this.ZWL.toDouble()),
    )
}

fun List<Rate>.toRates(): Rates {
    val map = this.associateBy { it.code }
    return Rates(
        AED = map["AED"]!!.value,
        AFN = map["AFN"]!!.value,
        ALL = map["ALL"]!!.value,
        AMD = map["AMD"]!!.value,
        ANG = map["ANG"]!!.value,
        AOA = map["AOA"]!!.value,
        ARS = map["ARS"]!!.value,
        AUD = map["AUD"]!!.value,
        AWG = map["AWG"]!!.value,
        AZN = map["AZN"]!!.value,
        BAM = map["BAM"]!!.value,
        BBD = map["BBD"]!!.value,
        BDT = map["BDT"]!!.value,
        BGN = map["BGN"]!!.value,
        BHD = map["BHD"]!!.value,
        BIF = map["BIF"]!!.value,
        BMD = map["BMD"]!!.value,
        BND = map["BND"]!!.value,
        BOB = map["BOB"]!!.value,
        BRL = map["BRL"]!!.value,
        BSD = map["BSD"]!!.value,
        BTC = map["BTC"]!!.value,
        BTN = map["BTN"]!!.value,
        BWP = map["BWP"]!!.value,
        BYN = map["BYN"]!!.value,
        BYR = map["BYR"]!!.value,
        BZD = map["BZD"]!!.value,
        CAD = map["CAD"]!!.value,
        CDF = map["CDF"]!!.value,
        CHF = map["CHF"]!!.value,
        CLF = map["CLF"]!!.value,
        CLP = map["CLP"]!!.value,
        CNY = map["CNY"]!!.value,
        COP = map["COP"]!!.value,
        CRC = map["CRC"]!!.value,
        CUC = map["CUC"]!!.value,
        CUP = map["CUP"]!!.value,
        CVE = map["CVE"]!!.value,
        CZK = map["CZK"]!!.value,
        DJF = map["DJF"]!!.value,
        DKK = map["DKK"]!!.value,
        DOP = map["DOP"]!!.value,
        DZD = map["DZD"]!!.value,
        EGP = map["EGP"]!!.value,
        ERN = map["ERN"]!!.value,
        ETB = map["ETB"]!!.value,
        EUR = map["EUR"]!!.value,
        FJD = map["FJD"]!!.value,
        FKP = map["FKP"]!!.value,
        GBP = map["GBP"]!!.value,
        GEL = map["GEL"]!!.value,
        GGP = map["GGP"]!!.value,
        GHS = map["GHS"]!!.value,
        GIP = map["GIP"]!!.value,
        GMD = map["GMD"]!!.value,
        GNF = map["GNF"]!!.value,
        GTQ = map["GTQ"]!!.value,
        GYD = map["GYD"]!!.value,
        HKD = map["HKD"]!!.value,
        HNL = map["HNL"]!!.value,
        HRK = map["HRK"]!!.value,
        HTG = map["HTG"]!!.value,
        HUF = map["HUF"]!!.value,
        IDR = map["IDR"]!!.value,
        ILS = map["ILS"]!!.value,
        IMP = map["IMP"]!!.value,
        INR = map["INR"]!!.value,
        IQD = map["IQD"]!!.value,
        IRR = map["IRR"]!!.value,
        ISK = map["ISK"]!!.value,
        JEP = map["JEP"]!!.value,
        JMD = map["JMD"]!!.value,
        JOD = map["JOD"]!!.value,
        JPY = map["JPY"]!!.value,
        KES = map["KES"]!!.value,
        KGS = map["KGS"]!!.value,
        KHR = map["KHR"]!!.value,
        KMF = map["KMF"]!!.value,
        KPW = map["KPW"]!!.value,
        KRW = map["KRW"]!!.value,
        KWD = map["KWD"]!!.value,
        KYD = map["KYD"]!!.value,
        KZT = map["KZT"]!!.value,
        LAK = map["LAK"]!!.value,
        LBP = map["LBP"]!!.value,
        LKR = map["LKR"]!!.value,
        LRD = map["LRD"]!!.value,
        LSL = map["LSL"]!!.value,
        LTL = map["LTL"]!!.value,
        LVL = map["LVL"]!!.value,
        LYD = map["LYD"]!!.value,
        MAD = map["MAD"]!!.value,
        MDL = map["MDL"]!!.value,
        MGA = map["MGA"]!!.value,
        MKD = map["MKD"]!!.value,
        MMK = map["MMK"]!!.value,
        MNT = map["MNT"]!!.value,
        MOP = map["MOP"]!!.value,
        MRO = map["MRO"]!!.value,
        MUR = map["MUR"]!!.value,
        MVR = map["MVR"]!!.value,
        MWK = map["MWK"]!!.value,
        MXN = map["MXN"]!!.value,
        MYR = map["MYR"]!!.value,
        MZN = map["MZN"]!!.value,
        NAD = map["NAD"]!!.value,
        NGN = map["NGN"]!!.value,
        NIO = map["NIO"]!!.value,
        NOK = map["NOK"]!!.value,
        NPR = map["NPR"]!!.value,
        NZD = map["NZD"]!!.value,
        OMR = map["OMR"]!!.value,
        PAB = map["PAB"]!!.value,
        PEN = map["PEN"]!!.value,
        PGK = map["PGK"]!!.value,
        PHP = map["PHP"]!!.value,
        PKR = map["PKR"]!!.value,
        PLN = map["PLN"]!!.value,
        PYG = map["PYG"]!!.value,
        QAR = map["QAR"]!!.value,
        RON = map["RON"]!!.value,
        RSD = map["RSD"]!!.value,
        RUB = map["RUB"]!!.value,
        RWF = map["RWF"]!!.value,
        SAR = map["SAR"]!!.value,
        SBD = map["SBD"]!!.value,
        SCR = map["SCR"]!!.value,
        SDG = map["SDG"]!!.value,
        SEK = map["SEK"]!!.value,
        SGD = map["SGD"]!!.value,
        SHP = map["SHP"]!!.value,
        SLE = map["SLE"]!!.value,
        SLL = map["SLL"]!!.value,
        SOS = map["SOS"]!!.value,
        SRD = map["SRD"]!!.value,
        STD = map["STD"]!!.value,
        SYP = map["SYP"]!!.value,
        SZL = map["SZL"]!!.value,
        THB = map["THB"]!!.value,
        TJS = map["TJS"]!!.value,
        TMT = map["TMT"]!!.value,
        TND = map["TND"]!!.value,
        TOP = map["TOP"]!!.value,
        TRY = map["TRY"]!!.value,
        TTD = map["TTD"]!!.value,
        TWD = map["TWD"]!!.value,
        TZS = map["TZS"]!!.value,
        UAH = map["UAH"]!!.value,
        UGX = map["UGX"]!!.value,
        USD = map["USD"]!!.value,
        UYU = map["UYU"]!!.value,
        UZS = map["UZS"]!!.value,
        VEF = map["VEF"]!!.value,
        VES = map["VES"]!!.value,
        VND = map["VND"]!!.value,
        VUV = map["VUV"]!!.value,
        WST = map["WST"]!!.value,
        XAF = map["XAF"]!!.value,
        XAG = map["XAG"]!!.value,
        XAU = map["XAU"]!!.value,
        XCD = map["XCD"]!!.value,
        XDR = map["XDR"]!!.value,
        XOF = map["XOF"]!!.value,
        XPF = map["XPF"]!!.value,
        YER = map["YER"]!!.value,
        ZAR = map["ZAR"]!!.value,
        ZMK = map["ZMK"]!!.value,
        ZMW = map["ZMW"]!!.value,
        ZWL = map["ZWL"]!!.value,
    )
}

fun Rates.getValueFromCode(
    code: String
) = when (code) {
    "AED" -> this.AED
    "AFN" -> this.AFN
    "ALL" -> this.ALL
    "AMD" -> this.AMD
    "ANG" -> this.ANG
    "AOA" -> this.AOA
    "ARS" -> this.ARS
    "AUD" -> this.AUD
    "AWG" -> this.AWG
    "AZN" -> this.AZN
    "BAM" -> this.BAM
    "BBD" -> this.BBD
    "BDT" -> this.BDT
    "BGN" -> this.BGN
    "BHD" -> this.BHD
    "BIF" -> this.BIF
    "BMD" -> this.BMD
    "BND" -> this.BND
    "BOB" -> this.BOB
    "BRL" -> this.BRL
    "BSD" -> this.BSD
    "BTC" -> this.BTC
    "BTN" -> this.BTN
    "BWP" -> this.BWP
    "BYN" -> this.BYN
    "BYR" -> this.BYR
    "BZD" -> this.BZD
    "CAD" -> this.CAD
    "CDF" -> this.CDF
    "CHF" -> this.CHF
    "CLF" -> this.CLF
    "CLP" -> this.CLP
    "CNY" -> this.CNY
    "COP" -> this.COP
    "CRC" -> this.CRC
    "CUC" -> this.CUC
    "CUP" -> this.CUP
    "CVE" -> this.CVE
    "CZK" -> this.CZK
    "DJF" -> this.DJF
    "DKK" -> this.DKK
    "DOP" -> this.DOP
    "DZD" -> this.DZD
    "EGP" -> this.EGP
    "ERN" -> this.ERN
    "ETB" -> this.ETB
    "EUR" -> this.EUR
    "FJD" -> this.FJD
    "FKP" -> this.FKP
    "GBP" -> this.GBP
    "GEL" -> this.GEL
    "GGP" -> this.GGP
    "GHS" -> this.GHS
    "GIP" -> this.GIP
    "GMD" -> this.GMD
    "GNF" -> this.GNF
    "GTQ" -> this.GTQ
    "GYD" -> this.GYD
    "HKD" -> this.HKD
    "HNL" -> this.HNL
    "HRK" -> this.HRK
    "HTG" -> this.HTG
    "HUF" -> this.HUF
    "IDR" -> this.IDR
    "ILS" -> this.ILS
    "IMP" -> this.IMP
    "INR" -> this.INR
    "IQD" -> this.IQD
    "IRR" -> this.IRR
    "ISK" -> this.ISK
    "JEP" -> this.JEP
    "JMD" -> this.JMD
    "JOD" -> this.JOD
    "JPY" -> this.JPY
    "KES" -> this.KES
    "KGS" -> this.KGS
    "KHR" -> this.KHR
    "KMF" -> this.KMF
    "KPW" -> this.KPW
    "KRW" -> this.KRW
    "KWD" -> this.KWD
    "KYD" -> this.KYD
    "KZT" -> this.KZT
    "LAK" -> this.LAK
    "LBP" -> this.LBP
    "LKR" -> this.LKR
    "LRD" -> this.LRD
    "LSL" -> this.LSL
    "LTL" -> this.LTL
    "LVL" -> this.LVL
    "LYD" -> this.LYD
    "MAD" -> this.MAD
    "MDL" -> this.MDL
    "MGA" -> this.MGA
    "MKD" -> this.MKD
    "MMK" -> this.MMK
    "MNT" -> this.MNT
    "MOP" -> this.MOP
    "MRO" -> this.MRO
    "MUR" -> this.MUR
    "MVR" -> this.MVR
    "MWK" -> this.MWK
    "MXN" -> this.MXN
    "MYR" -> this.MYR
    "MZN" -> this.MZN
    "NAD" -> this.NAD
    "NGN" -> this.NGN
    "NIO" -> this.NIO
    "NOK" -> this.NOK
    "NPR" -> this.NPR
    "NZD" -> this.NZD
    "OMR" -> this.OMR
    "PAB" -> this.PAB
    "PEN" -> this.PEN
    "PGK" -> this.PGK
    "PHP" -> this.PHP
    "PKR" -> this.PKR
    "PLN" -> this.PLN
    "PYG" -> this.PYG
    "QAR" -> this.QAR
    "RON" -> this.RON
    "RSD" -> this.RSD
    "RUB" -> this.RUB
    "RWF" -> this.RWF
    "SAR" -> this.SAR
    "SBD" -> this.SBD
    "SCR" -> this.SCR
    "SDG" -> this.SDG
    "SEK" -> this.SEK
    "SGD" -> this.SGD
    "SHP" -> this.SHP
    "SLE" -> this.SLE
    "SLL" -> this.SLL
    "SOS" -> this.SOS
    "SRD" -> this.SRD
    "STD" -> this.STD
    "SYP" -> this.SYP
    "SZL" -> this.SZL
    "THB" -> this.THB
    "TJS" -> this.TJS
    "TMT" -> this.TMT
    "TND" -> this.TND
    "TOP" -> this.TOP
    "TRY" -> this.TRY
    "TTD" -> this.TTD
    "TWD" -> this.TWD
    "TZS" -> this.TZS
    "UAH" -> this.UAH
    "UGX" -> this.UGX
    "USD" -> this.USD
    "UYU" -> this.UYU
    "UZS" -> this.UZS
    "VEF" -> this.VEF
    "VES" -> this.VES
    "VND" -> this.VND
    "VUV" -> this.VUV
    "WST" -> this.WST
    "XAF" -> this.XAF
    "XAG" -> this.XAG
    "XAU" -> this.XAU
    "XCD" -> this.XCD
    "XDR" -> this.XDR
    "XOF" -> this.XOF
    "XPF" -> this.XPF
    "YER" -> this.YER
    "ZAR" -> this.ZAR
    "ZMK" -> this.ZMK
    "ZMW" -> this.ZMW
    "ZWL" -> this.ZWL
    else -> null
}

fun Rates.updateRatesUsingCode(
    code: String, value: Double
) = when (code) {
    "AED" -> this.copy(AED = value)
    "AFN" -> this.copy(AFN = value)
    "ALL" -> this.copy(ALL = value)
    "AMD" -> this.copy(AMD = value)
    "ANG" -> this.copy(ANG = value)
    "AOA" -> this.copy(AOA = value)
    "ARS" -> this.copy(ARS = value)
    "AUD" -> this.copy(AUD = value)
    "AWG" -> this.copy(AWG = value)
    "AZN" -> this.copy(AZN = value)
    "BAM" -> this.copy(BAM = value)
    "BBD" -> this.copy(BBD = value)
    "BDT" -> this.copy(BDT = value)
    "BGN" -> this.copy(BGN = value)
    "BHD" -> this.copy(BHD = value)
    "BIF" -> this.copy(BIF = value)
    "BMD" -> this.copy(BMD = value)
    "BND" -> this.copy(BND = value)
    "BOB" -> this.copy(BOB = value)
    "BRL" -> this.copy(BRL = value)
    "BSD" -> this.copy(BSD = value)
    "BTC" -> this.copy(BTC = value)
    "BTN" -> this.copy(BTN = value)
    "BWP" -> this.copy(BWP = value)
    "BYN" -> this.copy(BYN = value)
    "BYR" -> this.copy(BYR = value)
    "BZD" -> this.copy(BZD = value)
    "CAD" -> this.copy(CAD = value)
    "CDF" -> this.copy(CDF = value)
    "CHF" -> this.copy(CHF = value)
    "CLF" -> this.copy(CLF = value)
    "CLP" -> this.copy(CLP = value)
    "CNY" -> this.copy(CNY = value)
    "COP" -> this.copy(COP = value)
    "CRC" -> this.copy(CRC = value)
    "CUC" -> this.copy(CUC = value)
    "CUP" -> this.copy(CUP = value)
    "CVE" -> this.copy(CVE = value)
    "CZK" -> this.copy(CZK = value)
    "DJF" -> this.copy(DJF = value)
    "DKK" -> this.copy(DKK = value)
    "DOP" -> this.copy(DOP = value)
    "DZD" -> this.copy(DZD = value)
    "EGP" -> this.copy(EGP = value)
    "ERN" -> this.copy(ERN = value)
    "ETB" -> this.copy(ETB = value)
    "EUR" -> this.copy(EUR = value)
    "FJD" -> this.copy(FJD = value)
    "FKP" -> this.copy(FKP = value)
    "GBP" -> this.copy(GBP = value)
    "GEL" -> this.copy(GEL = value)
    "GGP" -> this.copy(GGP = value)
    "GHS" -> this.copy(GHS = value)
    "GIP" -> this.copy(GIP = value)
    "GMD" -> this.copy(GMD = value)
    "GNF" -> this.copy(GNF = value)
    "GTQ" -> this.copy(GTQ = value)
    "GYD" -> this.copy(GYD = value)
    "HKD" -> this.copy(HKD = value)
    "HNL" -> this.copy(HNL = value)
    "HRK" -> this.copy(HRK = value)
    "HTG" -> this.copy(HTG = value)
    "HUF" -> this.copy(HUF = value)
    "IDR" -> this.copy(IDR = value)
    "ILS" -> this.copy(ILS = value)
    "IMP" -> this.copy(IMP = value)
    "INR" -> this.copy(INR = value)
    "IQD" -> this.copy(IQD = value)
    "IRR" -> this.copy(IRR = value)
    "ISK" -> this.copy(ISK = value)
    "JEP" -> this.copy(JEP = value)
    "JMD" -> this.copy(JMD = value)
    "JOD" -> this.copy(JOD = value)
    "JPY" -> this.copy(JPY = value)
    "KES" -> this.copy(KES = value)
    "KGS" -> this.copy(KGS = value)
    "KHR" -> this.copy(KHR = value)
    "KMF" -> this.copy(KMF = value)
    "KPW" -> this.copy(KPW = value)
    "KRW" -> this.copy(KRW = value)
    "KWD" -> this.copy(KWD = value)
    "KYD" -> this.copy(KYD = value)
    "KZT" -> this.copy(KZT = value)
    "LAK" -> this.copy(LAK = value)
    "LBP" -> this.copy(LBP = value)
    "LKR" -> this.copy(LKR = value)
    "LRD" -> this.copy(LRD = value)
    "LSL" -> this.copy(LSL = value)
    "LTL" -> this.copy(LTL = value)
    "LVL" -> this.copy(LVL = value)
    "LYD" -> this.copy(LYD = value)
    "MAD" -> this.copy(MAD = value)
    "MDL" -> this.copy(MDL = value)
    "MGA" -> this.copy(MGA = value)
    "MKD" -> this.copy(MKD = value)
    "MMK" -> this.copy(MMK = value)
    "MNT" -> this.copy(MNT = value)
    "MOP" -> this.copy(MOP = value)
    "MRO" -> this.copy(MRO = value)
    "MUR" -> this.copy(MUR = value)
    "MVR" -> this.copy(MVR = value)
    "MWK" -> this.copy(MWK = value)
    "MXN" -> this.copy(MXN = value)
    "MYR" -> this.copy(MYR = value)
    "MZN" -> this.copy(MZN = value)
    "NAD" -> this.copy(NAD = value)
    "NGN" -> this.copy(NGN = value)
    "NIO" -> this.copy(NIO = value)
    "NOK" -> this.copy(NOK = value)
    "NPR" -> this.copy(NPR = value)
    "NZD" -> this.copy(NZD = value)
    "OMR" -> this.copy(OMR = value)
    "PAB" -> this.copy(PAB = value)
    "PEN" -> this.copy(PEN = value)
    "PGK" -> this.copy(PGK = value)
    "PHP" -> this.copy(PHP = value)
    "PKR" -> this.copy(PKR = value)
    "PLN" -> this.copy(PLN = value)
    "PYG" -> this.copy(PYG = value)
    "QAR" -> this.copy(QAR = value)
    "RON" -> this.copy(RON = value)
    "RSD" -> this.copy(RSD = value)
    "RUB" -> this.copy(RUB = value)
    "RWF" -> this.copy(RWF = value)
    "SAR" -> this.copy(SAR = value)
    "SBD" -> this.copy(SBD = value)
    "SCR" -> this.copy(SCR = value)
    "SDG" -> this.copy(SDG = value)
    "SEK" -> this.copy(SEK = value)
    "SGD" -> this.copy(SGD = value)
    "SHP" -> this.copy(SHP = value)
    "SLE" -> this.copy(SLE = value)
    "SLL" -> this.copy(SLL = value)
    "SOS" -> this.copy(SOS = value)
    "SRD" -> this.copy(SRD = value)
    "STD" -> this.copy(STD = value)
    "SYP" -> this.copy(SYP = value)
    "SZL" -> this.copy(SZL = value)
    "THB" -> this.copy(THB = value)
    "TJS" -> this.copy(TJS = value)
    "TMT" -> this.copy(TMT = value)
    "TND" -> this.copy(TND = value)
    "TOP" -> this.copy(TOP = value)
    "TRY" -> this.copy(TRY = value)
    "TTD" -> this.copy(TTD = value)
    "TWD" -> this.copy(TWD = value)
    "TZS" -> this.copy(TZS = value)
    "UAH" -> this.copy(UAH = value)
    "UGX" -> this.copy(UGX = value)
    "USD" -> this.copy(USD = value)
    "UYU" -> this.copy(UYU = value)
    "UZS" -> this.copy(UZS = value)
    "VEF" -> this.copy(VEF = value)
    "VES" -> this.copy(VES = value)
    "VND" -> this.copy(VND = value)
    "VUV" -> this.copy(VUV = value)
    "WST" -> this.copy(WST = value)
    "XAF" -> this.copy(XAF = value)
    "XAG" -> this.copy(XAG = value)
    "XAU" -> this.copy(XAU = value)
    "XCD" -> this.copy(XCD = value)
    "XDR" -> this.copy(XDR = value)
    "XOF" -> this.copy(XOF = value)
    "XPF" -> this.copy(XPF = value)
    "YER" -> this.copy(YER = value)
    "ZAR" -> this.copy(ZAR = value)
    "ZMK" -> this.copy(ZMK = value)
    "ZMW" -> this.copy(ZMW = value)
    "ZWL" -> this.copy(ZWL = value)
    else -> this
}