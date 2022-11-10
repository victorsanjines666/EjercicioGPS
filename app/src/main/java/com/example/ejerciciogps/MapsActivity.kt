package com.example.ejerciciogps

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.ejerciciogps.Coordenadas.avaroa
import com.example.ejerciciogps.Coordenadas.hospitalObrero
import com.example.ejerciciogps.Coordenadas.jardinJapones
import com.example.ejerciciogps.Coordenadas.lapaz
import com.example.ejerciciogps.Coordenadas.plazaIsabelCatolica
import com.example.ejerciciogps.Coordenadas.univalle
import com.example.ejerciciogps.Coordenadas.valleLuna

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.ejerciciogps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener {

    //Variable global que representa su mapa de Google
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.binding = binding

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //Concepto clave y avanzado de Android.
        //Fragmentos: seccionar en piezas el diseño de UI
        //Fragmento que contiene el mapa de Google
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        //El mapa de Google se obtine de manera asíncrona
        //busca cargar el mapa sin congelar tu pantalla
        mapFragment.getMapAsync(this)
        setupToggleButtons()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //Lo que contiene se ejecuta cuando el mapa
    //esta listo y cargado en tu pantalla
    //como parámetro recibes un objeto que contiene
    //el mapa listo para trabajar........
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //una posición.
        //se maneja un objeto que conjunciona latitud y longitud
        //ese objeto se llama LatLng
        val egipto = LatLng(29.977704826716288, 31.13258127087077)

        //Definir algunas configuraciones de su mapa
        //Los mapas manejan un zoom y este va de 0 a 21
        //donde jamas vas a poder usar el valor 0 o 1
        //el zoom minimo es de 2.....
        //el zoom máximo es de 20
        /*
            zoom de 20: se usa para edificios, construcciones
                        casas, parques, detalles muy finos.
            a partir de 15: se suele usar para calles
            a partir de 10: para ciudades
            a partir de 5: para paises y continente
            a partir de 2: continente y su masa continental
         */
        mMap.apply {
            //configurar el zoom mínimo que pueden usar en su mapa
            setMinZoomPreference(15f)
            //El máximo zoom que puede hacer el usuario
            setMaxZoomPreference(20f)
        }

        //Uso de marcadores o las famosas tachuelas rojas
        mMap.addMarker(MarkerOptions()
            .title("Mi viaje de ensueño")
            .snippet("${egipto.latitude}, ${egipto.longitude}")
            .position(egipto)
        )

        //Realizando el movimiento de la cámara a la posición
        //deseada por usted
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(egipto))
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(egipto, 18f))

        //Configurar una cámara personalizada
        /*val cameraUnivalle = CameraPosition.Builder()
            .target(univalle)//ubicación donde va a centrarse la cámara
            .zoom(16f)
            .tilt(45f)//ángulo de inclinación de la cámara
            .bearing(245f)// ángulo para cambiar orientación de vista del mapa 0-360
            .build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraUnivalle))
         */
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(univalle, 18f))

        //Movimiento de cámara utilizando procesos en background
        //utilizando Corrutinas
        /*lifecycleScope.launch {
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(avaroa,18f))
            delay(3_500)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(jardinJapones, 18f))
        }*/
        //Corrutina movimiento en la cámara pixel por pixel
        /*lifecycleScope.launch {
            delay(3_500)
            for (i in 0 .. 50) {
                mMap.animateCamera(
                    CameraUpdateFactory.scrollBy(0f, 150f)
                )
                delay(1_000)
            }
        }*/


        /**
         * Sesgo de mapas
         * Delimitar o concentrar al mapa en una zona determinada
         * Bounds
         */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lapaz, 12f))
        //se basa en las coordenadas noreste y suroeste de la región donde
        //quieren trabajar
        val lapazBounds = LatLngBounds(plazaIsabelCatolica, hospitalObrero)
        lifecycleScope.launch {
            delay(4_000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(lapazBounds, 32))
            //punto central del cuadrante
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lapazBounds.center, 18f))
        }
        //Sesgando el mapa a solo interacción en esa región Bounds
        mMap.setLatLngBoundsForCameraTarget(lapazBounds)

        /**
         * Controles de UI del mapa y Gestures del mapa
         */
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true // botones de Zoom_in y Zoom_out
            isCompassEnabled = true // ver la brujula y la orientación del mapa
            isRotateGesturesEnabled = false //impide la rotación del mapa
            isMapToolbarEnabled = true// habilitar ir al mapa para ver rutas o tu marcador
        }

        //Darle un padding al mapa para hacer
        //que sus controles no se solapen con nuestro
        //menù de botones
        mMap.setPadding(0,0,0, Utils.dp(64))

        //Para ver trafico de la ciudad
        //mMap.isTrafficEnabled = true

        /**
         * configuración de estilo personalizado de mapa
         */
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
            R.raw.my_map_style))

        /**
         * configuración de marcadores:
         *  1) estilos, settings
         *  2) Eventos al marcador
         */
        val univalleMarcador = mMap.addMarker(MarkerOptions()
            .position(univalle)
            .title("Mi universidad")
        )
        univalleMarcador?.run {
            //setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))//cambiar color a marcador
            //setIcon(BitmapDescriptorFactory.defaultMarker(310f))//cambiar color a marcador
            //setIcon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant))// icono personalizado
            Utils.getBitmapFromVector(this@MapsActivity, R.drawable.ic_terrain_48)?.let {
                setIcon(BitmapDescriptorFactory.fromBitmap(it))
            }
            rotation = 75f
            setAnchor(0.5f, 0.5f)
            isFlat = true // define si el marcador rota o no con el mapa
            isDraggable = true
        }
        //Eventos a los marcadores
        //Evento click en el marker
        mMap.setOnMarkerClickListener(this)
        //Evento drag del marcador
        mMap.setOnMarkerDragListener(this)

        /**
         * Trazo de lineas entre puntos de coordenadas
         * denominada Polyline
         */
        setupPolyline()



        //Vamos a configurar el evento más simple
        //y útil de los mapas de google
        // el click en cualquier lugar del mapa
        mMap.setOnMapClickListener {
            mMap.addMarker(MarkerOptions()
                .title("Posicion Random")
                .snippet("${it.latitude}, ${it.longitude}")
                .draggable(true)
                .position(it)
            )
        }
    }

    private fun setupPolyline() {
        //tener un arreglo o lista de puntos para trazar la linea
        //Una ruta se compone de la unión de múltiples líneas
        val routes = mutableListOf(univalle, plazaIsabelCatolica, jardinJapones, valleLuna)
        val polyline = mMap.addPolyline(PolylineOptions()
            .color(Color.YELLOW)
            .width(15f)// ancho de la linea
            .clickable(true)// que puedes hacerle click a la ruta o mejor dicho a la linea
            .geodesic(true)// la curvatura de la tierra, la linea se curva con el radio de la tierra
        )
        //Para dibujar la linea se usa el atributo points
        polyline.points = routes
    }

    /**
     * Configuración de menú de botones
     */
    private fun setupToggleButtons() {
        binding.toggleGroup.addOnButtonCheckedListener {
                group, checkedId, isChecked ->
            if (isChecked) {
                //Cambiar el tipo de mapa
                mMap.mapType = when(checkedId) {
                    R.id.btnNormal -> GoogleMap.MAP_TYPE_NORMAL
                    R.id.btnHibrido -> GoogleMap.MAP_TYPE_HYBRID
                    R.id.btnSatelital -> GoogleMap.MAP_TYPE_SATELLITE
                    R.id.btnTerreno -> GoogleMap.MAP_TYPE_TERRAIN
                    else -> GoogleMap.MAP_TYPE_NONE
                }
            }

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(this,
            "${marker.position.latitude}, ${marker.position.longitude}",
        Toast.LENGTH_LONG).show()
        return false
    }

    //Mientras es arrastrado el marcador
    override fun onMarkerDrag(marker: Marker) {
        //marker es el marcador que estas arrastrando
        //binding.toggleGruop.visibility = View.INVISIBLE
        binding.toggleGroup.visibility = View.INVISIBLE
        marker.alpha = 0.4f
    }

    //cuando sueltas el marcador despues de haberlo arrastrado
    override fun onMarkerDragEnd(marker: Marker) {
        marker.alpha = 1.0f
        binding.toggleGroup.visibility = View.VISIBLE
        //Los marcadores tienen una ventana de información
        // que se la conoce como  infoWindow
        //o ventana de información del marcador
        marker.showInfoWindow()
    }

    override fun onMarkerDragStart(marker: Marker) {
        //oculta la ventana de información del marcador
        marker.hideInfoWindow()
    }
}








