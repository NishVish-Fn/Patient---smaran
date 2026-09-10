package net.kibotu.geofencerelay.features.ai.localization

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

data class LanguageItem(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String
)

object MultilingualManager {

    val supportedLanguages = listOf(
        LanguageItem("en", "English", "English", "🌐"),
        LanguageItem("hi", "Hindi", "हिंदी", "🇮🇳"),
        LanguageItem("as", "Assamese", "অসমীয়া", "🌿"),
        LanguageItem("lus", "Mizo", "Mizo ṭawng", "🏔️"),
        LanguageItem("kha", "Khasi", "Ka Ktien Khasi", "🌲"),
        LanguageItem("mni", "Manipuri", "মৈতৈলোন্", "🌺"),
        LanguageItem("nag", "Nagamese", "Nagamese", "⛰️")
    )

    // Full UI String Translations Dictionary
    private val stringRepository = mapOf(
        // App header & subtitle
        "app_title" to mapOf(
            "en" to "Smaran",
            "hi" to "स्मरण (Smaran)",
            "as" to "স্মৰণ (Smaran)",
            "lus" to "Smaran Hriatna",
            "kha" to "Smaran Jingkynmaw",
            "mni" to "স্মরণ (Smaran)",
            "nag" to "Smaran Dimag"
        ),
        "beacon_live" to mapOf(
            "en" to "Live Beacon Active",
            "hi" to "लाइव बीकन सक्रिय",
            "as" to "লাইভ বিকন সক্ৰিয়",
            "lus" to "Location Thawn Mek",
            "kha" to "Ka Jingithuh Shai",
            "mni" to "লাইভ বিকন এক্টিভ",
            "nag" to "Live Beacon Chalu Ase"
        ),
        "beacon_standby" to mapOf(
            "en" to "Beacon Standby",
            "hi" to "बीकन स्टैंडबाय",
            "as" to "বিকন অপক্ষাৰত",
            "lus" to "Inring Reirawh",
            "kha" to "Pynsngap Shuwa",
            "mni" to "বিকন লেপ্লি",
            "nag" to "Beacon Standby Ase"
        ),

        // Tile 1: GPS Beacon
        "tile_gps_title" to mapOf(
            "en" to "GPS Beacon",
            "hi" to "जीपीएस बीकन",
            "as" to "জি.পি.এছ. বিকন",
            "lus" to "GPS Hmun Zawnna",
            "kha" to "Ka Jingbuh Hmun GPS",
            "mni" to "GPS মফম তাকপা",
            "nag" to "GPS Beacon Jagah"
        ),
        "tile_gps_sub_broadcasting" to mapOf(
            "en" to "Transmitting Live",
            "hi" to "लाइव प्रसारण चालू",
            "as" to "লাইভ প্ৰেৰণ চলিছে",
            "lus" to "Thawn Mek A Ni",
            "kha" to "Phah Jingtip Mynta",
            "mni" to "লাইভ ত্রান্সমিৎ তৌরি",
            "nag" to "Live Bheji Ase"
        ),
        "tile_gps_sub_standby" to mapOf(
            "en" to "Tap to Broadcast",
            "hi" to "प्रसारण हेतु टैप करें",
            "as" to "প্ৰেৰণ কৰিবলৈ টিপক",
            "lus" to "Thawn Tan Nan Hmet",
            "kha" to "Kyntuit Ban Phah",
            "mni" to "শন্দোক্নবা নম্বিয়ু",
            "nag" to "Chalu Kuribo Karone Dababi"
        ),

        // Tile 2: Brain Games
        "tile_games_title" to mapOf(
            "en" to "Brain Games",
            "hi" to "मस्तिष्क खेल",
            "as" to "মগজুৰ খেল",
            "lus" to "Rilru Infiamna",
            "kha" to "Ki Jingialehkai Jingmut",
            "mni" to "ৱাখল্লোন শান্নবা",
            "nag" to "Dimag laga Khel"
        ),
        "tile_games_sub" to mapOf(
            "en" to "2 Cognitive Modes",
            "hi" to "२ संज्ञानात्मक खेल",
            "as" to "২টা মগজুৰ অনুশীলন",
            "lus" to "Infiamna Chi 2",
            "kha" to "Ar Tylli Ki Jingialehkai",
            "mni" to "মখল অনি শান্নবা",
            "nag" to "2 Ta Khel Ase"
        ),

        // Tile 3: Cognitive Score
        "tile_score_title" to mapOf(
            "en" to "Cognitive Health",
            "hi" to "संज्ञानात्मक स्वास्थ्य",
            "as" to "মগজুৰ স্বাস্থ্য",
            "lus" to "Hriatna Hriselna",
            "kha" to "Ka Koit Ka Khiah Jingmut",
            "mni" to "ৱাখলগী হকশেল",
            "nag" to "Dimag laga Health"
        ),
        "tile_score_sub_untested" to mapOf(
            "en" to "Play Game to Assess",
            "hi" to "स्कोर हेतु खेल खेलें",
            "as" to "স্ক'ৰৰ বাবে খেল খেলক",
            "lus" to "Zir Chian Nan Infiam",
            "kha" to "Lehkai Ban Pynshai",
            "mni" to "স্কোরগীদমক শান্নবিয়ু",
            "nag" to "Score Karone Khelibi"
        ),
        "tile_score_sub_tested" to mapOf(
            "en" to "Assessment Ready",
            "hi" to "आकलन तैयार",
            "as" to "মূল্যায়ন প্ৰস্তুত",
            "lus" to "Endikna A Kim",
            "kha" to "La Dep Ka Jingbishar",
            "mni" to "য়েংশিনবা লোইরে",
            "nag" to "Score Ahi Jaise"
        ),

        // Tile 4: Safety Alerts
        "tile_safety_title" to mapOf(
            "en" to "Safety & Alerts",
            "hi" to "सुरक्षा एवं अलर्ट",
            "as" to "সুৰক্ষা আৰু সতৰ্কতা",
            "lus" to "Venhimna & Hriattirna",
            "kha" to "Ka Jingiada & Jingma",
            "mni" to "য়াম্না কনবা চেকশিনবা",
            "nag" to "Safety aru Alert"
        ),
        "tile_safety_sub" to mapOf(
            "en" to "Sundowning & Safe Zone",
            "hi" to "संध्या सिंड्रोम व सुरक्षित घेरा",
            "as" to "সন্ধ্যা সতর্কতা ও সুৰক্ষিত বলয়",
            "lus" to "Tlailam & Hmun Him",
            "kha" to "Jingpeit Syngam & Hmun Bha",
            "mni" to "নুমিদাং চেকশিনবা মফম",
            "nag" to "Shaam laga Alert aru Safe Zone"
        ),

        // Tile 5: Voice & Languages
        "tile_voice_title" to mapOf(
            "en" to "Languages & Voice",
            "hi" to "भाषाएं और आवाज",
            "as" to "ভাষা আৰু মাত",
            "lus" to "Tawng & Aw",
            "kha" to "Ki Ktien & Ka Jingkren",
            "mni" to "লোন অমসুং খোন্থোক",
            "nag" to "Bhasha aru Awaaz"
        ),
        "tile_voice_sub" to mapOf(
            "en" to "7 Regional Languages",
            "hi" to "७ क्षेत्रीय भाषाएं",
            "as" to "৭টা আঞ্চলিক ভাষা",
            "lus" to "Hnam Tawng 7",
            "kha" to "7 Tylli Ki Ktien",
            "mni" to "লমদমগী লোন ৭",
            "nag" to "7 Ta Local Bhasha"
        ),

        // Tile 6: Memory Vault
        "tile_memory_title" to mapOf(
            "en" to "Memory Vault",
            "hi" to "स्मृति संदूक",
            "as" to "স্মৃতি ভঁৰাল",
            "lus" to "Hriatrengna Hmun",
            "kha" to "Ka Synduk Jingkynmaw",
            "mni" to "নিংশিং মফম",
            "nag" to "Yaad laga Tijori"
        ),
        "tile_memory_sub" to mapOf(
            "en" to "Family Recall Therapy",
            "hi" to "पारिवारिक यादें",
            "as" to "পৰিয়ালৰ স্মৃতি সান্নিধ্য",
            "lus" to "Chhungkua Hriatletna",
            "kha" to "Jingkynmaw Iingsem",
            "mni" to "ইমুংগী নিংশিং থৌরম",
            "nag" to "Parivar laga Yaad"
        ),

        // Common Buttons & Actions
        "btn_back" to mapOf(
            "en" to "Back",
            "hi" to "वापस",
            "as" to "উভতি যাওক",
            "lus" to "Kir Leh",
            "kha" to "Leit Dien",
            "mni" to "হন্দোকপা",
            "nag" to "Pise Jabi"
        ),
        "btn_play_again" to mapOf(
            "en" to "Play Another Round",
            "hi" to "एक और राउंड खेलें",
            "as" to "আন এটি ৰাউণ্ড খেলক",
            "lus" to "Infiam Leh Rawh",
            "kha" to "Lehkai Biang Sa Chisien",
            "mni" to "অমুক হন্না শান্নবিয়ু",
            "nag" to "Aru Ek Bar Khelibi"
        ),
        "btn_start_test" to mapOf(
            "en" to "Play Brain Game to Assess",
            "hi" to "आकलन हेतु खेल शुरू करें",
            "as" to "মূল্যায়নৰ বাবে খেল আৰম্ভ কৰক",
            "lus" to "Infiamna Tan Rawh",
            "kha" to "Sdang Ia Ka Jingialehkai",
            "mni" to "শেন্নবগীদমক শান্নবা হৌবিয়ু",
            "nag" to "Khel Kheli Kene Test Kuri Lobi"
        ),
        "btn_listen_voice" to mapOf(
            "en" to "Hear Encouraging Voice",
            "hi" to "प्रोत्साहन ध्वनि सुनें",
            "as" to "উৎসাহজনক মাত শুনক",
            "lus" to "Tawngkam Phurna Ngaithla",
            "kha" to "Sngap Ia Ka Jingpynshai",
            "mni" to "থৌনা হাপ্পা খোন্থোক তাবিয়ু",
            "nag" to "Bhal Awaaz Huni Lobi"
        ),

        // Safety Bottom Pill
        "safety_bottom_pill" to mapOf(
            "en" to "Caregiver Safety & Safe Zone",
            "hi" to "देखभालकर्ता सुरक्षा व सुरक्षित घेरा",
            "as" to "যত্নলোৱাৰ সুৰক্ষা আৰু সুৰক্ষিত বলয়",
            "lus" to "Enkawltu Venhimna & Hmun Him",
            "kha" to "Ka Jingiada & Hmun Bha",
            "mni" to "য়েংশিনবগী চেকশিন মফম",
            "nag" to "Caregiver Safety aru Safe Zone"
        ),

        // Game Hub Strings
        "games_hub_title" to mapOf(
            "en" to "Cognitive Exercise Hub",
            "hi" to "संज्ञानात्मक व्यायाम केंद्र",
            "as" to "মগজুৰ অনুশীলন কেন্দ্ৰ",
            "lus" to "Rilru Zirna Hmun",
            "kha" to "Ka Hmun Pynshait Jingmut",
            "mni" to "ৱাখলগী কান্নবা শান্নফম",
            "nag" to "Dimag Kasrat Hub"
        ),
        "games_hub_sub" to mapOf(
            "en" to "Choose your daily cognitive journey",
            "hi" to "अपनी दैनिक दिमागी गतिविधि चुनें",
            "as" to "আপোনাৰ দৈনিক মগজুৰ খেল বাছনি কৰক",
            "lus" to "Vawiin i infiam duh thlang rawh",
            "kha" to "Jied ia ka jingialehkai jong phi",
            "mni" to "অদোমগী নোংমগী শান্নবা খনবিয়ু",
            "nag" to "Aji laga dimag khel basi lobi"
        ),
        "game1_name" to mapOf(
            "en" to "Memory Card Match",
            "hi" to "स्मृति कार्ड मिलान",
            "as" to "স্মৃতি কাৰ্ড মিলন",
            "lus" to "Thlalak Inmil Zawn",
            "kha" to "Pyniasnoh Ia Ki Kot",
            "mni" to "কার্দ নিংশিংবা অমসুং মান্নবা",
            "nag" to "Card Yaad Matching"
        ),
        "game1_desc" to mapOf(
            "en" to "Tap cards to uncover matching pairs",
            "hi" to "जोड़े खोजने के लिए कार्डों पर टैप करें",
            "as" to "যোৰা মিলাবলৈ কাৰ্ডবোৰ টিপক",
            "lus" to "A inmil zawng chhuak rawh",
            "kha" to "Kyntuit ban shem ia kiba iadei",
            "mni" to "মান্নবা ফংনবগীদমক কার্দশিং তাপীউ",
            "nag" to "Mili thaka juri paishe dababi"
        ),
        "game2_name" to mapOf(
            "en" to "Pattern & Sequence Logic",
            "hi" to "पैटर्न एवं अनुक्रम तर्क",
            "as" to "ক্ৰমিক প্ৰতিৰূপ যুক্তি",
            "lus" to "A Dawt Zawnna Hriatna",
            "kha" to "Jingithuh Ia Ka Jingiaid",
            "mni" to "মথং-মনাও নৈশিনবা ৱাখল",
            "nag" to "Pattern aru Sequence Logic"
        ),
        "game2_desc" to mapOf(
            "en" to "Memorize and repeat the glowing sequence",
            "hi" to "चमकते क्रम को याद रखें और दोहराएं",
            "as" to "জ্বলা ক্ৰমটো মনত ৰাখক আৰু পুনৰাবৃত্তি কৰক",
            "lus" to "Eng zuih zuih kha vawng reng la zawm rawh",
            "kha" to "Kynmaw ia ka jingthaba bad leh biang",
            "mni" to "ঙাল্লক্লিবা মথং অদু নিংশিংদুনা শান্নবিয়ু",
            "nag" to "Chamak thaka sequence yaad kuri kene dababi"
        ),
        "sequence_watch" to mapOf(
            "en" to "Watch closely...",
            "hi" to "ध्यान से देखें...",
            "as" to "মনোযোগেৰে চাওক...",
            "lus" to "Ngun takin en rawh...",
            "kha" to "Peit bha...",
            "mni" to "চেকশিন্না য়েংবিয়ু...",
            "nag" to "Dhyan te sabibi..."
        ),
        "sequence_your_turn" to mapOf(
            "en" to "Now repeat the pattern!",
            "hi" to "अब वही क्रम दोहराएं!",
            "as" to "এতিয়া অনুক্ৰমটো পুনৰাবৃত্তি কৰক!",
            "lus" to "I hun a thleng ve ta, zawm rawh!",
            "kha" to "Mynta ka dei ka pali jong phi!",
            "mni" to "হৌজিক অদোমগী খোঙথাংনি!",
            "nag" to "Etiya apuni laga bari, repeat koribi!"
        ),

        // Beacon Panel
        "beacon_title" to mapOf(
            "en" to "Live GPS Sentinel",
            "hi" to "लाइव जीपीएस प्रहरी",
            "as" to "লাইভ জি.পি.এছ. প্ৰহৰী",
            "lus" to "GPS Venhimna",
            "kha" to "Ka Jingithuh Shai GPS",
            "mni" to "লাইভ GPS য়েংশিনবা",
            "nag" to "Live GPS Sentinel"
        ),
        "beacon_subtitle" to mapOf(
            "en" to "Real-Time GPS & Caregiver Telemetry",
            "hi" to "रीयल-टाइम जीपीएस व देखभालकर्ता टेलीमेट्री",
            "as" to "প্ৰকৃত সময়ৰ জি.পি.এছ. আৰু যত্নলোৱাৰ তথ্য",
            "lus" to "GPS & Enkawltu Hriattirna",
            "kha" to "Jingtip Halor Ka Hmun & Nongsumar",
            "mni" to "লাইভ GPS অমসুং য়েংশিনবগী ঈ-পাউ",
            "nag" to "Real-Time GPS aru Caregiver Telemetry"
        ),
        "btn_turn_on_gps" to mapOf(
            "en" to "Turn ON Device GPS",
            "hi" to "डिवाइस जीपीएस चालू करें",
            "as" to "ডিভাইচৰ জি.পি.এছ. অন কৰক",
            "lus" to "GPS On Rawh",
            "kha" to "Plie Ia Ka GPS",
            "mni" to "GPS অন তৌবিয়ু",
            "nag" to "Device GPS On Koribi"
        ),
        "btn_grant_perms" to mapOf(
            "en" to "Grant Location Permissions",
            "hi" to "स्थान अनुमति प्रदान करें",
            "as" to "স্থানৰ অনুমতি প্ৰদান কৰক",
            "lus" to "Location Phallatna Pe Rawh",
            "kha" to "Ai Jingbit Ban Tip Ia Ka Hmun",
            "mni" to "মফম তাকপগী অয়াবা পীবিয়ু",
            "nag" to "Location Permission Dibi"
        ),
        "btn_start_broadcast" to mapOf(
            "en" to "Start Live Broadcasting",
            "hi" to "लाइव प्रसारण शुरू करें",
            "as" to "লাইভ সম্প্ৰচাৰ আৰম্ভ কৰক",
            "lus" to "Thawn Tan Rawh",
            "kha" to "Sdang Ban Phah",
            "mni" to "লাইভ শন্দোকপা হৌবিয়ু",
            "nag" to "Live Broadcasting Chalu Koribi"
        ),
        "btn_stop_broadcast" to mapOf(
            "en" to "Stop Broadcasting",
            "hi" to "प्रसारण रोकें",
            "as" to "সম্প্ৰচাৰ বন্ধ কৰক",
            "lus" to "Thawn Tihtawpna",
            "kha" to "Sangeh Ban Phah",
            "mni" to "শন্দোকপা লেপখ্রবু",
            "nag" to "Broadcasting Bondho Koribi"
        ),
        "lbl_address" to mapOf(
            "en" to "Physical Address",
            "hi" to "भौतिक पता",
            "as" to "ঠিকনা",
            "lus" to "Hmun Hming",
            "kha" to "Ka Haka Kaba Shisha",
            "mni" to "লৈফমগী ঠিকনা",
            "nag" to "Asol Thikana"
        ),
        "lbl_coordinates" to mapOf(
            "en" to "GPS Coordinates",
            "hi" to "जीपीएस निर्देशांक",
            "as" to "জি.পি.এছ. স্থানাংক",
            "lus" to "GPS Hmun Chhinna",
            "kha" to "Ki Dak Jingbuh GPS",
            "mni" to "GPS কোওর্ডিনেত",
            "nag" to "GPS Coordinates"
        ),
        "lbl_battery" to mapOf(
            "en" to "Battery Level",
            "hi" to "बैटरी स्तर",
            "as" to "বেটাৰীৰ মাত্ৰা",
            "lus" to "Battery San Zawng",
            "kha" to "Ka Bor Ka Battery",
            "mni" to "বেত্তরীগী চাং",
            "nag" to "Battery Level"
        ),
        "lbl_speed" to mapOf(
            "en" to "Movement Speed",
            "hi" to "गति की रफ़्तार",
            "as" to "গতিৰ বেগ",
            "lus" to "Kal Chak Zawng",
            "kha" to "Ka Jingstet Ka Jingiaid",
            "mni" to "খোঙজেলগী য়াম্বা",
            "nag" to "Speed"
        ),
        "lbl_safe_zone" to mapOf(
            "en" to "Safe Zone Status",
            "hi" to "सुरक्षित क्षेत्र स्थिति",
            "as" to "সুৰক্ষিত মণ্ডলৰ স্থিতি",
            "lus" to "Hmun Him Dinhmun",
            "kha" to "Ka Hmun Bha",
            "mni" to "চেকশিন মফমগী ফীভম",
            "nag" to "Safe Zone laga Halat"
        ),
        "status_inside_safe_zone" to mapOf(
            "en" to "Inside Safe Zone",
            "hi" to "सुरक्षित घेरे के अंदर",
            "as" to "সুৰক্ষিত বলয়ৰ ভিতৰত",
            "lus" to "Hmun Him Chhungah",
            "kha" to "Hapoh Ka Hmun Ba Bha",
            "mni" to "চেকশিন মফম মনুংদা",
            "nag" to "Safe Zone te Ase"
        ),
        "status_breach" to mapOf(
            "en" to "🚨 OUTSIDE SAFE ZONE",
            "hi" to "🚨 सुरक्षित घेरे से बाहर",
            "as" to "🚨 সুৰক্ষিত বলয়ৰ বাহিৰত",
            "lus" to "🚨 HMUN HIM PAWN",
            "kha" to "🚨 SHA BAR KA HMUN",
            "mni" to "🚨 চেকশিন মফম মপানদা",
            "nag" to "🚨 SAFE ZONE BAHAR"
        ),

        // Safety Panel
        "safety_title" to mapOf(
            "en" to "Safety & Emergency",
            "hi" to "सुरक्षा एवं आपातकालीन",
            "as" to "সুৰক্ষা আৰু জৰুৰীকালীন",
            "lus" to "Venhimna & Chhanhimna",
            "kha" to "Ka Jingiada & Jingeh",
            "mni" to "য়াম্না কনবা চেকশিনবা",
            "nag" to "Safety aru Emergency"
        ),
        "safety_subtitle" to mapOf(
            "en" to "Emergency SOS, Home Directions & Wander Alerts",
            "hi" to "आपातकालीन एसओएस, घर का रास्ता व भटकाव अलर्ट",
            "as" to "জৰুৰীকালীন SOS, ঘৰলৈ দিশ আৰু পথভ্ৰষ্ট সতৰ্কতা",
            "lus" to "SOS, In Panna Kawng & Hriattirna",
            "kha" to "SOS, Ka Lynti Sha Iing & Jingpeit",
            "mni" to "SOS, য়ুমগী লম্বী অমসুং চেকশিনবা",
            "nag" to "Emergency SOS, Ghor laga Rasta aru Alert"
        ),
        "btn_take_me_home" to mapOf(
            "en" to "TAKE ME HOME (Directions)",
            "hi" to "मुझे घर ले चलो (रास्ता)",
            "as" to "মোক ঘৰলৈ লৈ যাওক (দিশ)",
            "lus" to "IN AH MIN HRUAIRAW (Kawng)",
            "kha" to "IALAM SHA IING (Lynti)",
            "mni" to "য়ুমদা পুখ্রবু (লম্বী)",
            "nag" to "MOKE GHOR LOI JABI (Rasta)"
        ),
        "btn_set_home" to mapOf(
            "en" to "Set Home Safe Zone",
            "hi" to "घर का सुरक्षित स्थान सेट करें",
            "as" to "ঘৰৰ সুৰক্ষিত স্থান নিৰ্ধাৰণ কৰক",
            "lus" to "In Hmun Him Siambawl",
            "kha" to "Buh Ia Ka Iing Bha",
            "mni" to "য়ুমগী চেকশিন মফম সেমগৎলু",
            "nag" to "Ghor laga Safe Zone Set Koribi"
        ),
        "btn_use_current_gps" to mapOf(
            "en" to "Use Current GPS Location as Home",
            "hi" to "वर्तमान जीपीएस स्थान को घर के रूप में उपयोग करें",
            "as" to "বৰ্তমানৰ জি.পি.এছ. অৱস্থান ঘৰ হিচাপে ব্যৱহাৰ কৰক",
            "lus" to "Tunlai GPS hmang hian in siam rawh",
            "kha" to "Pyndonkam ia ka GPS mynta kum ka iing",
            "mni" to "হৌজিক্কী GPS অসি য়ুম ওইনা লৌবিয়ু",
            "nag" to "Etiya laga GPS jagah ghor hisabte lobo"
        ),
        "btn_save_home" to mapOf(
            "en" to "Save Home Location",
            "hi" to "घर का स्थान सहेजें",
            "as" to "ঘৰৰ অৱস্থান সংৰক্ষণ কৰক",
            "lus" to "In Hmun Vawng Tha Rawh",
            "kha" to "Kynshew Ia Ka Hmun Iing",
            "mni" to "য়ুমগী মফম সংৰক্ষণ তৌবিয়ু",
            "nag" to "Ghor laga Jagah Save Koribi"
        ),

        // Memory Vault Panel
        "memory_title" to mapOf(
            "en" to "Memory Vault",
            "hi" to "स्मृति संदूक",
            "as" to "স্মৃতি ভঁৰাল",
            "lus" to "Hriatrengna Hmun",
            "kha" to "Ka Synduk Jingkynmaw",
            "mni" to "নিংশিং মফম",
            "nag" to "Yaad laga Tijori"
        ),
        "memory_subtitle" to mapOf(
            "en" to "Autobiographical Family Recall",
            "hi" to "पारिवारिक यादें और संस्मरण",
            "as" to "পৰিয়ালৰ স্মৃতি সান্নিধ্য",
            "lus" to "Chhungkua Hriatletna",
            "kha" to "Jingkynmaw Iingsem",
            "mni" to "ইমুংগী নিংশিং থৌরম",
            "nag" to "Parivar laga Yaad"
        ),
        "btn_add_memory" to mapOf(
            "en" to "Add Memory",
            "hi" to "याद जोड़ें",
            "as" to "স্মৃতি যোগ কৰক",
            "lus" to "Hriatna Belh",
            "kha" to "Buh Jingkynmaw",
            "mni" to "নিংশিংবা হাপচিল্লু",
            "nag" to "Yaad Milabi"
        ),
        "lbl_no_memories" to mapOf(
            "en" to "No Memory Cards Yet",
            "hi" to "अभी कोई स्मृति कार्ड नहीं है",
            "as" to "এতিয়ালৈকে কোনো স্মৃতি কাৰ্ড নাই",
            "lus" to "Hriatrengna Card A La Awm Lo",
            "kha" to "Ym Pat Don Kot Jingkynmaw",
            "mni" to "নিংশিং কার্দ অমত্তা লৈত্রি",
            "nag" to "Kiba Yaad Card Nai Etiya"
        ),
        "lbl_no_memories_desc" to mapOf(
            "en" to "Family members can add custom recall questions and moments above!",
            "hi" to "परिवार के सदस्य ऊपर अपनी तस्वीरें और संस्मरण प्रश्न जोड़ सकते हैं!",
            "as" to "পৰিয়ালৰ সদস্যসকলে ওপৰত স্মৃতিমূলক প্ৰশ্ন যোগ কৰিব পাৰে!",
            "lus" to "Chhungte hian zawhna leh thlalak an dah lut thei e!",
            "kha" to "Ki bahaing ki lah ban buh ki jingkylli kynmaw!",
            "mni" to "ইমুংগী মীশিংনা য়াথং অদু হাপচিনবা য়াগনি!",
            "nag" to "Ghor laga manu khan upar te photo aru prashna milabo pare!"
        ),

        // Health Score Panel
        "health_title" to mapOf(
            "en" to "Cognitive Health Score",
            "hi" to "संज्ञानात्मक स्वास्थ्य स्कोर",
            "as" to "মগজুৰ স্বাস্থ্য মূল্যায়ন",
            "lus" to "Hriatna Hriselna Score",
            "kha" to "Ka Jingkhein Koit Khiah Jingmut",
            "mni" to "ৱাখলগী হকশেল স্কোর",
            "nag" to "Dimag Health Score"
        ),
        "health_subtitle" to mapOf(
            "en" to "Clinical Telemetry Analysis (SIH26003)",
            "hi" to "क्लिनिकल टेलीमेट्री विश्लेषण",
            "as" to "চিকিৎসাভিত্তিক তথ্য বিশ্লেষণ",
            "lus" to "Enkawlna Lam Endikna",
            "kha" to "Jingbishar Koit Khiah",
            "mni" to "হকশেলগী নৈশিনবা",
            "nag" to "Clinical Telemetry Analysis"
        ),
        "lbl_untested" to mapOf(
            "en" to "No Assessment Recorded Yet Today",
            "hi" to "आज अभी तक कोई आकलन दर्ज नहीं है",
            "as" to "আজি এতিয়ালৈকে কোনো মূল্যায়ন হোৱা নাই",
            "lus" to "Vawiin Endikna A La Awm Lo",
            "kha" to "Ym Pat Don Jingkhein Mynta",
            "mni" to "ঙসি অমত্তা য়েংশিনদ্রি",
            "nag" to "Aji kiba test kora nai"
        ),
        "lbl_untested_desc" to mapOf(
            "en" to "Complete any daily cognitive exercise to compute your genuine clinical CPS score.",
            "hi" to "अपना वास्तविक नैदानिक सीपीएस स्कोर जानने के लिए कोई भी दैनिक खेल खेलें।",
            "as" to "আপোনাৰ প্ৰকৃত ক্লিনিকল চি.পি.এছ. স্ক'ৰ গণনা কৰিবলৈ যিকোনো খেল সম্পূৰ্ণ কৰক।",
            "lus" to "CPS score hre turin infiamna vawi khat tal khel rawh.",
            "kha" to "Lehkai ban ioh ia ka CPS score.",
            "mni" to "CPS স্কোর ফংনবগীদমক অদোম শান্নবিয়ু।",
            "nag" to "Apuni laga CPS score pabo karone kiba ekta khel khelibi."
        ),

        // Games Hub Panel
        "game3_title" to mapOf(
            "en" to "Color-Word Stroop Focus",
            "hi" to "रंग-शब्द स्ट्रोप एकाग्रता",
            "as" to "ৰং-শব্দ ষ্ট্ৰুপ মনোযোগ",
            "lus" to "Rawng & Thu Inmil Zawn",
            "kha" to "Ka Rong Bad Ktien Jingpyrkhat",
            "mni" to "মচু অমসুং ৱাহৈ নৈশিনবা",
            "nag" to "Rang aru Kotha Focus"
        ),
        "game3_desc" to mapOf(
            "en" to "10 fast-paced rounds evaluating executive cognitive inhibition",
            "hi" to "१० तीव्र राउंड मानसिक अवरोध व एकाग्रता हेतु",
            "as" to "মনোযোগ বৃদ্ধিৰ বাবে ১০টা তীব্ৰ ৰাউণ্ড",
            "lus" to "Rilru sawizawi nan vawi 10 khelh tur",
            "kha" to "10 tylli ki jingialehkai ban pynkhlain jingmut",
            "mni" to "ৱাখলগী কান্নবা রাউন্দ ১০",
            "nag" to "10 ta round dimag focus karone"
        ),
        "game4_title" to mapOf(
            "en" to "Ascending Number Trail",
            "hi" to "आरोही संख्या पथ",
            "as" to "উৰ্ধ্বমুখী সংখ্যাৰ ক্ৰম",
            "lus" to "Number Inzawm Zawn",
            "kha" to "Ki Dak Jingkhein Kiew",
            "mni" to "মশীংগী মথং-মনাও লম্বী",
            "nag" to "Number Trail Khel"
        ),
        "game4_desc" to mapOf(
            "en" to "Multi-round sequence trail evaluating visual scanning & motor speed",
            "hi" to "दृष्टि व गतिशीलता हेतु बहु-राउंड संख्या क्रम",
            "as" to "দৃষ্টি আৰু মানসিক গতিৰ বাবে সংখ্যা ক্ৰম",
            "lus" to "Mit leh kut inmil zawnna",
            "kha" to "Pyniasnoh ia ki dak jingkhein",
            "mni" to "মিৎ অমসুং খুৎকী চৎনবা",
            "nag" to "Number scan aru speed khel"
        ),
        "lbl_reminder_interval" to mapOf(
            "en" to "Game Reminder Interval",
            "hi" to "खेल अनुस्मारक अंतराल",
            "as" to "খেলৰ সোঁৱৰণিৰ ব্যৱধান",
            "lus" to "Infiamna Hriattirna Hun",
            "kha" to "Ka Por Pynkynmaw Lehkai",
            "mni" to "শান্নবগী নিংশিংবা মতম",
            "nag" to "Game Reminder Time"
        ),
        "lbl_reminder_interval_desc" to mapOf(
            "en" to "Plays loud alarm sound and displays popping colors even when app is closed",
            "hi" to "ऐप बंद होने पर भी तेज़ अलार्म बजेगा और रंग दिखाई देंगे",
            "as" to "এপ বন্ধ থাকিলেও উচ্চ শব্দত এলার্ম বাজিব আৰু ৰং জিলিকিব",
            "lus" to "App khar mahse ri ring tak leh rawng mawi tak a lo lang ang",
            "kha" to "Wat la khang ia ka app, kan sawa bad pyni rong",
            "mni" to "এপ থিংজিল্লবসু খুন্থোক কেন্না তারগনি",
            "nag" to "App bondho thakile bhi awaz aru rong ulai jabo"
        ),
        "btn_test_alarm" to mapOf(
            "en" to "Test Loud Alarm Now (3s)",
            "hi" to "अभी तेज़ अलार्म का परीक्षण करें (3s)",
            "as" to "এতিয়াই উচ্চ এলার্ম পৰীক্ষা কৰক (৩ ছেকেণ্ড)",
            "lus" to "Alarm Chhin Chhinna (3s)",
            "kha" to "Pyrshang Ia Ka Alarm Mynta (3s)",
            "mni" to "এলার্ম চাংয়েং তৌবিয়ু (৩s)",
            "nag" to "Etiya Loud Alarm Test Koribi (3s)"
        ),
        "lbl_round" to mapOf(
            "en" to "Round",
            "hi" to "राउंड",
            "as" to "ৰাউণ্ড",
            "lus" to "Round",
            "kha" to "Kyntien",
            "mni" to "রাউন্দ",
            "nag" to "Round"
        ),
        "lbl_of" to mapOf(
            "en" to "of",
            "hi" to "का",
            "as" to "/",
            "lus" to "/",
            "kha" to "na",
            "mni" to "/",
            "nag" to "porate"
        ),
        "lbl_score" to mapOf(
            "en" to "Score",
            "hi" to "अंक",
            "as" to "স্ক'ৰ",
            "lus" to "Score",
            "kha" to "Jingioh",
            "mni" to "স্কোর",
            "nag" to "Score"
        ),
        "lbl_level_cleared" to mapOf(
            "en" to "Level Cleared! Next level...",
            "hi" to "स्तर पूरा हुआ! अगला स्तर...",
            "as" to "স্তৰ সম্পূৰ্ণ! পৰৱৰ্তী স্তৰ...",
            "lus" to "I zo ta! A dawt leh...",
            "kha" to "La dep! Kawei pat...",
            "mni" to "লেভেল লোইরে! মথংগী...",
            "nag" to "Level Pass Hoise! Aru aage..."
        ),
        "game1_hint" to mapOf(
            "en" to "Tap cards to uncover matching pairs",
            "hi" to "जोड़े खोजने के लिए कार्डों पर टैप करें",
            "as" to "যোৰা মিলাবলৈ কাৰ্ডবোৰ টিপক",
            "lus" to "A inmil zawng chhuak rawh",
            "kha" to "Pyniasnoh ia kiba iadei",
            "mni" to "মান্নবা কার্দশিং খনবিয়ু",
            "nag" to "Card juri milabi"
        ),
        "game3_hint" to mapOf(
            "en" to "Tap the INK COLOR",
            "hi" to "स्याही का रंग चुनें",
            "as" to "চিয়াহীৰ ৰংটো বাছক",
            "lus" to "A rawng dik thlang rawh",
            "kha" to "Jied ia ka rong shisha",
            "mni" to "মচু অদু খনবিয়ু",
            "nag" to "Rang basi lobi"
        ),
        "game4_hint" to mapOf(
            "en" to "Tap numbers in ascending order",
            "hi" to "बढ़ते क्रम में संख्याओं पर टैप करें",
            "as" to "সংখ্যাবোৰ ক্ৰমানুসাৰে টিপক",
            "lus" to "Number inzawm indawtin hmet rawh",
            "kha" to "Kyntuit ia ki dak jingkhein",
            "mni" to "মশীং মথং-মনাও নম্বিয়ু",
            "nag" to "Number ekta ekta dababi"
        ),

        // Full Screen Alarm Screen
        "alarm_title" to mapOf(
            "en" to "BRAIN EXERCISE TIME",
            "hi" to "मस्तिष्क व्यायाम का समय",
            "as" to "মগজুৰ অনুশীলনৰ সময়",
            "lus" to "RILRU INFIAHNA HUN",
            "kha" to "KA POR LEHKAI JINGMUT",
            "mni" to "ৱাখলগী এক্সরসাইজগী মতম",
            "nag" to "DIMAG KASRAT LAGA TIME"
        ),
        "alarm_desc" to mapOf(
            "en" to "Keep your mind sharp! It's time for your scheduled memory and focus exercises.",
            "hi" to "अपने दिमाग को तेज़ रखें! यह आपके निर्धारित स्मृति और ध्यान अभ्यास का समय है।",
            "as" to "আপোনাৰ মন সজীৱ ৰাখক! স্মৃতি আৰু মনোযোগ বৃদ্ধিৰ খেল খেলক।",
            "lus" to "I rilru tiharh rawh! Hriatna leh rilru sawizawi hun a thleng ta.",
            "kha" to "Pynshait ia ka jingmut! Ka dei ka por ban pynkhlain ia ka jingkynmaw.",
            "mni" to "অদোমগী ৱাখল শেমগৎলু! নিংশিং অমসুং পুক্নিং চংবগী মতম ওইরে।",
            "nag" to "Dimag tez rakhabi! Apuni laga yaad aru dhyan khel laga time hoise."
        ),
        "alarm_btn_play" to mapOf(
            "en" to "START BRAIN GAME NOW",
            "hi" to "दिमागी खेल शुरू करें",
            "as" to "মগজুৰ খেল আৰম্ভ কৰক",
            "lus" to "INFIAMNA TAN RAWH",
            "kha" to "SDANG LEHKAI MYNTA",
            "mni" to "হৌজিক শান্নবা হৌবিয়ু",
            "nag" to "ETIYA DIMAG KHEL CHALU KORIBI"
        ),
        "alarm_btn_snooze" to mapOf(
            "en" to "Snooze for 10 Minutes",
            "hi" to "१० मिनट बाद याद दिलाएं",
            "as" to "১০ মিনিটৰ পিছত সোঁৱৰাব",
            "lus" to "Minute 10 hnuah hriattir leh rawh",
            "kha" to "10 minit pynsangeh",
            "mni" to "মিনিত ১০ তুংদা নিংশিংবিয়ু",
            "nag" to "10 minute pise yaad dibi"
        )
    )

    private val encouragements = mapOf(
        "gentle" to mapOf(
            "en" to "Wonderful effort! You are doing great. Let's enjoy another fun memory activity!",
            "hi" to "बहुत सुंदर प्रयास! आप बहुत अच्छा खेल रहे हैं। चलिए अगला मजेदार स्मृति खेल खेलते हैं!",
            "as" to "অতি সুন্দৰ! আপুনি বহুত ভাল খেলিছে। বলক আন এটি ধুনীয়া স্মৃতি খেল খেলো।",
            "lus" to "Thawk tha tak tling i ni! A nuam dang i zir zel ang u.",
            "kha" to "Ka jingseimot kaba bha shibun! To ngin ia iaid shakhmat paralok.",
            "mni" to "য়াম্না ফবা হোৎনবনি! অদোম য়াম্না ফনা শান্নরি। মথংগী হরাওবা নিংশিং শান্নবা শান্নসি!",
            "nag" to "Bhal kosish ase! Apuni bhal kheli ase. Aru ekta bhal dimag kheli khelibo ahibi!"
        ),
        "encouraging" to mapOf(
            "en" to "Fantastic progress! Your focus is super sharp today. Let's keep exploring!",
            "hi" to "शानदार प्रगति! आपका ध्यान आज बहुत तेज़ है। चलिए आगे बढ़ते हैं!",
            "as" to "চমৎকার উন্নতি! আপোনাৰ মনোযোগ সঁচাকৈয়ে প্রশংসনীয়।",
            "lus" to "I puitlinna a tha hle mai! I rilru a fim tha hle.",
            "kha" to "Ka jingkiew kaba khraw! Ka jingmut jong phi ka long kaba shai halor kiei kiei.",
            "mni" to "চাউখৎপা খোঙথাংনি! ঙসিদি অদোমগী পুক্নিং য়াম্না থৌনা লৈ। মাংলোয়ননা চত্থসি!",
            "nag" to "Bishi bhal aguwai ase! Apuni laga dhyan aji bishi bhal ase. Aru aage jabi!"
        ),
        "celebratory" to mapOf(
            "en" to "Outperforming excellence! You are a master memory explorer today!",
            "hi" to "असाधारण प्रतिभा! आज आप वाकई एक महान स्मृति विजेता हैं!",
            "as" to "অসাধাৰণ দক্ষতা! আপুনি আজি সঁচাকৈয়ে এজন মহান স্মৃতি বিজয়ী!",
            "lus" to "A tha tawpkhawk hle mai! Vawiin chu i thluak a chak zual hle.",
            "kha" to "Ka jingshai kaba khraw tarn! Phi long u nongjop uba bakhraw ha ka jingkynmaw.",
            "mni" to "থোইদোক-হেন্দোকপা হৈশিংবনি! অদোম ঙসি নিংশিংবগী অচেৎপা মাইপাকপা অমনি!",
            "nag" to "Ekdum zabardast! Aji toh apuni asol dimag laga champion hoise!"
        )
    )

    private val voiceConfirmations = mapOf(
        "en" to "Voice guidance set to English.",
        "hi" to "ध्वनि मार्गदर्शन हिंदी में सेट किया गया है।",
        "as" to "অসমীয়া ভাষাত মাতৰ নিৰ্দেশনা সক্ৰিয় কৰা হৈছে।",
        "lus" to "Aw hmanga kaihhruaina hi Mizo tawngin siam a ni.",
        "kha" to "Jingpynshai ha ka jien Khasi la pynkyntu.",
        "mni" to "মৈতৈলোন্দা খোন্থোক্কী ৱাফম শেম্লে।",
        "nag" to "Awaaz guide toh Nagamese te set kurishey."
    )

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    fun initTts(context: Context) {
        if (tts != null) return
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                tts?.language = Locale.ENGLISH
            }
        }
    }

    fun speak(text: String, langCode: String = "en") {
        if (!isTtsReady || tts == null) return
        try {
            when (langCode) {
                "hi" -> tts?.language = Locale("hi", "IN")
                "as", "lus", "kha", "mni", "nag" -> {
                    val inLocale = Locale("en", "IN")
                    tts?.language = inLocale
                }
                else -> tts?.language = Locale.ENGLISH
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "encouragement_utterance")
        } catch (_: Exception) {}
    }

    /**
     * Translates any string key dynamically to the selected language.
     * Falls back to English if missing in target dialect.
     */
    fun tr(key: String, langCode: String): String {
        val entry = stringRepository[key] ?: return key
        return entry[langCode] ?: entry["en"] ?: key
    }

    fun getEncouragement(level: String, langCode: String): String {
        val tier = encouragements[level] ?: encouragements["encouraging"]!!
        return tier[langCode] ?: tier["en"] ?: "Wonderful effort! You are doing great!"
    }

    fun getVoiceConfirmation(langCode: String): String {
        return voiceConfirmations[langCode] ?: voiceConfirmations["en"]!!
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isTtsReady = false
        } catch (_: Exception) {}
    }
}
