package com.example.shisetup

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shisetup.adbdroid.Shield
import com.example.shisetup.adbdroid.ShieldResolution
import com.example.shisetup.adbdroid.ShieldUpscaling
import com.example.shisetup.netsense.Netsense
import com.example.shisetup.netsense.Netsense.Companion.getFromAddress
import com.example.shisetup.updater.Alauncher
import com.example.shisetup.updater.KodinerdsNexus
import com.example.shisetup.updater.KodinerdsOmega
import com.example.shisetup.updater.KodinerdsPiers
import com.example.shisetup.updater.SmartTube
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("SdCardPath")
class AndroidScreenViewModel(application: Application) : AndroidViewModel(application) {
    val context by lazy { getApplication<Application>().applicationContext }
    var address = mutableStateOf("192.168.1.44")
    var current = mutableStateOf(0.0f)
    var content = mutableStateOf("NOOOOOOOOONE")
    var error = mutableStateOf("NOOOOOOOOONE")
    var loading = mutableStateOf(false)
    var private = mutableStateOf("TOKEN_GOES_HERE")
    lateinit var machine: Shield

    fun onBackButtonClickedOld() = viewModelScope.launch {
        loading.value = true
        val manager = Shield(address.value, context = context)
        try {
            manager.runAttach()
            manager.runReboot()
            val address = "https://www.dropbox.com/s/f5dz07b4t4bm9s3/shield_dummies_20220710.zip?dl=0"
            val fetched = Netsense.getFromDropbox(address, context)
            manager.runUnpack(fetched.toString(), "/sdcard/DUMMIES")
            content.value = fetched.toString()
        } catch (e: Exception) {
            content.value = e.message.toString()
        }
        loading.value = false
    }

    suspend fun setAlauncher1() {
        content.value = "ALAUNCHER (1)"
        val deposit = "/sdcard/Download"
        val baseurl = "https://www.themoviedb.org/t/p/original"
        val updater = Alauncher(machine, context)
        machine.runInvoke("rm $deposit/*.jpg")
        listOf(
            "1gGRY9bnIc0Jaohgc6jNFidjgLK", // American Horror Story
            "kI9tiDhDpeav28nlwDwTUbUwiSx", // Avatar: The Way of Water
            "gDtZQmfzvErZpeXOVeCBQE9WkSF", // Doctor Who
            "4e36PN10oS3x2zJtE30Del0uEHS", // Krampus
            "hsEAnOKuESc9HJmIGbmKQk5mxmH", // Chainsaw Man
            "cXlyBXUg6G1qqHntUwkJAjdv8b9", // The Owl House
            "sBOenwOZGRN5nZZGw4TxwtnfrEf", // Violent Night
            "bPW6HcI0E1eklEle61LVQuD1Cwc", // Spider-Man: Brand New Day
            "8sSKdEmlmqF4kJUd28SqthXC4yZ", // Toy Story 5 (2026)
        ).forEach {
            delay(1000.milliseconds)
            val address = "https://www.themoviedb.org/t/p/original/$it.jpg"
            val fetched = getFromAddress(address, context)
            val distant = "/sdcard/Download/${fetched?.name}"
            machine.runExport(fetched!!.absolutePath, distant)
        }
        updater.runUpdate()
        updater.setWallpaper("$baseurl/8sSKdEmlmqF4kJUd28SqthXC4yZ.jpg")
    }

    suspend fun setAlauncher2() {
        content.value = "ALAUNCHER (2)"
        val updater = Alauncher(machine, context)
        updater.runUpdate()
        updater.runVanishCategories()
        updater.setCategory("_", 80)
        updater.setCategory("_", 120)
        updater.setCategory("_", 120)
        updater.setApplicationByIndex("Kodinerds", 3)
        updater.setApplicationByIndex("SmartTube beta", 3)
        updater.setApplicationByIndex("Kodinerds Omega", 3)
    }

    suspend fun setKodiEnglish() {
        content.value = "KODINERDS PIERS (ENGLISH)"
        machine.runFinish(KodinerdsNexus(machine, context).pkgname)
        machine.runFinish(KodinerdsPiers(machine, context).pkgname)
        val updater = KodinerdsOmega(machine, context)
        updater.runRemove()
        updater.runUpdate()
        updater.setPip(enabled = false)
        ///
        machine.runFinish(updater.pkgname)
        updater.setEstuaryColor("SKINDEFAULT")
        updater.setEstuaryMenuList(enabled = false)
        updater.setEstuaryFavourites(enabled = true)
        updater.setKodiAudioPassthrough(enabled = true)
        updater.setKodiEnableKeymapFix(enabled = true)
        updater.setKodiEnableUnknownSources(enabled = true)
        updater.setKodiSettingLevel("3")
        ///
        updater.setKodiWebserver(enabled = true, secured = false)
        updater.setKodiAfr(enabled = true)
        updater.setKodiAfrDelay(seconds = 3.5)
        updater.setKodiEnablePreferDefaultAudio(enabled = false)
        updater.setKodiEnableShowParentFolder(enabled = false)
        updater.setKodiEnableUpdateFromAnyRepositories(enabled = true)
        updater.setKodiLanguageForAudio("English")
        updater.setKodiLanguageForSubtitles("English")
        updater.runRpc(mapOf("jsonrpc" to "2.0", "method" to "Application.Quit", "params" to emptyMap<String, Any>(), "id" to 1))
        delay(5000.milliseconds)
        machine.runFinish(updater.pkgname)
        ///
        updater.setKodiWebserver(enabled = true, secured = false)
        updater.setCuminationAddon()
        updater.setOtakuAddon()
        updater.setPovAddon()
        updater.setUmbrellaAddon()
        delay(5000.milliseconds)
        updater.runRpc(mapOf("jsonrpc" to "2.0", "method" to "Application.Quit", "params" to emptyMap<String, Any>(), "id" to 1))
        delay(5000.milliseconds)
        machine.runFinish(updater.pkgname)
        ///
        updater.setOtakuAlldebridToken(private.value)
        updater.setPovAlldebridToken(private.value)
        updater.setUmbrellaAlldebridToken(private.value)
        updater.setUmbrellaExternalProvider()
        ///
        updater.setKodiWebserver(enabled = false, secured = true)
    }

    suspend fun setKodiFrench() {
        content.value = "KODINERDS OMEGA (FRENCH)"
        machine.runFinish(KodinerdsOmega(machine, context).pkgname)
        machine.runFinish(KodinerdsPiers(machine, context).pkgname)
        val updater = KodinerdsNexus(machine, context)
        updater.runRemove()
        updater.runUpdate()
        updater.setPip(enabled = false)
        ///
        machine.runFinish(updater.pkgname)
        updater.setEstuaryColor("SKINDEFAULT")
        updater.setEstuaryMenuList(enabled = false)
        updater.setEstuaryFavourites(enabled = true)
        updater.setKodiAudioPassthrough(enabled = true)
        updater.setKodiEnableKeymapFix(enabled = true)
        updater.setKodiEnableUnknownSources(enabled = true)
        updater.setKodiSettingLevel("3")
        ///
        updater.setKodiWebserver(enabled = true, secured = false)
        updater.setKodiAfr(enabled = true)
        updater.setKodiAfrDelay(seconds = 3.5)
        updater.setKodiEnablePreferDefaultAudio(enabled = true)
        updater.setKodiEnableShowParentFolder(enabled = false)
        updater.setKodiEnableUpdateFromAnyRepositories(enabled = true)
        updater.setKodiKeyboardList(listOf("French AZERTY"))
        updater.setKodiLanguageForAudio("default") // TODO: Check thoroughly
        updater.setKodiLanguageForSystem("fr_fr")
        updater.runRpc(mapOf("jsonrpc" to "2.0", "method" to "Application.Quit", "params" to emptyMap<String, Any>(), "id" to 1))
        delay(5000.milliseconds)
        machine.runFinish(updater.pkgname)
        ///
        updater.setKodiWebserver(enabled = true, secured = false)
        updater.setVstreamAddon()
        updater.runRpc(mapOf("jsonrpc" to "2.0", "method" to "Application.Quit", "params" to emptyMap<String, Any>(), "id" to 1))
        delay(5000.milliseconds)
        machine.runFinish(updater.pkgname)
        ///
        updater.setVstreamAlldebridToken(private.value)
        updater.setVstreamEnableActivateSubtitles(enabled = true)
        updater.setVstreamPastebinCodes()
        updater.setVstreamPastebinUrl()
        updater.setVstreamTmdbBackdropQuality("original")
        updater.setVstreamTmdbPosterQuality("original")
        ///
        updater.setKodiWebserver(enabled = false, secured = true)
    }

    suspend fun setShieldExperience() {
        content.value = "SHIELD EXPERIENCE"
        machine.setBloatware(enabled = false)
        machine.setGooglePlay()
        machine.setUpscaling(ShieldUpscaling.AI_HIGH)
        machine.setResolution(ShieldResolution.P2160_DOLBY_HZ59)
    }

    suspend fun setSmartTube() {
        content.value = "SMARTTUBE"
        val updater = SmartTube(machine, context)
        updater.runRemove()
        updater.runUpdate()
        updater.setPip(enabled = false)
    }

    fun onButtonClicked() = viewModelScope.launch {
        loading.value = true
        machine = Shield(address.value, context = context)
        try {
            machine.runAttach()
            machine.runEscape()

            // ASSERT
            // var command = "cat /sdcard/Android/data/net.kodinerds.maven.kodi21/files/.kodi/userdata/keymaps/keyboard.xml"
            // var command = "cat cat /sdcard/Android/data/net.kodinerds.maven.kodi21/files/.kodi/userdata/favourites.xml"
            // var command = "cat /sdcard/Android/data/net.kodinerds.maven.kodi21/files/.kodi/userdata/addon_data/plugin.video.vstream/settings.xml"
            // var command = "cat /sdcard/Android/data/net.kodinerds.maven.kodi22/files/.kodi/userdata/addon_data/plugin.video.pov/settings.xml"
            // var command = "cat /sdcard/Android/data/net.kodinerds.maven.kodi22/files/.kodi/temp/kodi.log"
            // var command = "cat /sdcard/Android/data/net.kodinerds.maven.kodi22/files/.kodi/userdata/addon_data/plugin.video.otaku/settings.xml"
//            val command = "ls /sdcard/Android/data/net.kodinerds.maven.kodi21/files/.kodi/addons"
//            val outputs = machine.runInvoke(command)

            // UPDATE
            setAlauncher1()
            // setShieldExperience()
            // setKodiEnglish()
            // setKodiFrench()
            // setSmartTube()
            setAlauncher2()

            // REBOOT
            content.value = "REBOOT"
            machine.runReboot()
        } catch (e: Exception) {
            error.value = e.message.toString()
        }
        loading.value = false
    }

    fun onContinueButtonClicked() = viewModelScope.launch {
        loading.value = true
        val machine = Shield(address.value, context = context)
        try {
            machine.runAttach()
            val spawned = machine.runInvoke("getprop ro.product.model")
            content.value = spawned.output.trim()
        } catch (e: Exception) {
            content.value = e.message.toString()
        }
        loading.value = false
    }
}