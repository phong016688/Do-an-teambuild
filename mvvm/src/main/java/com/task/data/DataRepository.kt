package com.task.data

import com.task.data.local.LocalRepository
import com.task.data.remote.RemoteRepository
import com.task.data.remote.dto.NewsModel
import javax.inject.Inject


/**
 * Created by AhmedEltaher
 */

class DataRepository @Inject
constructor(private val remoteRepository: RemoteRepository, private val localRepository: LocalRepository) : DataSource {

    override suspend fun requestNews(): Resource<NewsModel> {
        return remoteRepository.requestNews()
    }
}
