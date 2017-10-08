package server.model

case class User(id:Option[Long], username:String, password:String, role:String)
