package com.honeyapps.wordlebattles.utils

import io.github.cdimascio.dotenv.dotenv

val env = dotenv {
    directory = "/assets"
    filename = "env"
//    ignoreIfMalformed = true
//    ignoreIfMissing = true
}