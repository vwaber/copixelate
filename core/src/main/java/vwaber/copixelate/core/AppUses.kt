package vwaber.copixelate.core

object AppUses{

    private lateinit var dataSource: AppDataSource

    fun init(dataSource: AppDataSource){
        this.dataSource = dataSource
    }

    fun login(){
        dataSource.login()
    }

}
