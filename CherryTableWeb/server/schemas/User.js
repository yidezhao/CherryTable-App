var mongoose = require('mongoose');
var Schema = mongoose.Schema

var userSchema = new Schema ({
  name: {type: String, required: true, unique: true, index:true},
  email: {type: String, required: true, unique:true, lowercase:true},
  passwordHash: {type: String, required: true},
  profilePic: {data: Buffer, contentType: String},
  contact: {type: String, required: true},
  interests: [String],
  donations: [String]
},
{timestamps:true});

module.exports = mongoose.model('User', userSchema);
