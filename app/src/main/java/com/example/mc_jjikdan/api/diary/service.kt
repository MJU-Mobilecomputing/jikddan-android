package com.example.mc_jjikdan.api.diary

import com.example.mc_jjikdan.api.diary.interfaces.IDiaryService
import com.example.mc_jjikdan.api.instance.ApiInstance

class service {
    val instance: ApiInstance = ApiInstance()
    val DiaryService: IDiaryService = instance.CreateService(IDiaryService::class.java)
}