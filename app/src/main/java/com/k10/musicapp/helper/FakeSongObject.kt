package com.k10.musicapp.helper

import com.k10.musicapp.datamodel.SongObject

class FakeSongObject {
    companion object {
        fun getSongObject(): SongObject {
            val songObject = SongObject()
            songObject.songId = "id1"
            songObject.songName = "Song 1"
            songObject.singer = "Singer 1"
            songObject.songStreamUrl = ""
            songObject.songPosterUrl = ""
            return songObject
        }

        fun getSongList(): List<SongObject> {
            val list: ArrayList<SongObject> = ArrayList()
            for (i in 0..9) {
                list.add(
                    SongObject(
                        "id$i",
                        "Song_$i",
                        "Singer_$i",
                        songUrl[i]
                    )
                )
            }
            return list
        }

        //10
        private val songUrl: ArrayList<String> = arrayListOf(
            "https://mp3d.jamendo.com/?trackid=799037&format=mp32",
            "https://mp3d.jamendo.com/?trackid=799038&format=mp32",
            "https://mp3d.jamendo.com/?trackid=799039&format=mp32",
            "https://mp3d.jamendo.com/?trackid=736039&format=mp32",
            "https://mp3d.jamendo.com/?trackid=762155&format=mp32",
            "https://mp3d.jamendo.com/?trackid=35531&format=mp32",
            "https://mp3d.jamendo.com/?trackid=659732&format=mp32",
            "https://mp3d.jamendo.com/?trackid=976824&format=mp32",
            "https://mp3d.jamendo.com/?trackid=577953&format=mp32",
            "https://mp3d.jamendo.com/?trackid=578956&format=mp32"
            )

        //10
        private val posterUrl: ArrayList<String> = arrayListOf(
            "https://cdn.pixabay.com/photo/2018/05/21/12/49/clipart-3418189__340.png",
            "https://www.clker.com/cliparts/4/3/0/6/11949854421936090706farfalla_contorno_archit_01.svg.med.png",
            "https://image.shutterstock.com/image-illustration/cute-monkey-waving-260nw-361764005.jpg",
            "https://www.picgifs.com/clip-art/cartoons/pokemon/clip-art-pokemon-508076.jpg",
            "https://www.graphicsfactory.com/clip-art/image_files/image/8/1605848-jolly-roger-skull-and-bones-vector-clipart.jpg",
            "https://png.clipart.me/image_preview/c39/green-plant-concept-42773.jpg",
            "https://toppng.com/uploads/preview/sunglass-clipart-transparent-background-clip-art-sun-glasses-11563277336x0lnptfzsq.png",
            "https://thumbs.dreamstime.com/b/teddy-bear-head-icon-cute-toy-clipart-vector-illustration-cartoon-148527509.jpg",
            "https://classroomclipart.com/sm-black-white-outline-cheerleader-clipart.jpg",
            "https://image.freepik.com/free-vector/turtle-clip-art-vector-illustration_75817-616.jpg"
        )
    }
}