package se.lth.solid.vilmer.thirtygame

import java.io.Serializable
import java.lang.Math.random


class Score(var score: Int, var name: String = if (random() > .5) "JOHN" else "JANE") : Serializable
