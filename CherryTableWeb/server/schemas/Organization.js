var mongoose = require('mongoose');
var Schema = mongoose.Schema

var organizationSchema = new Schema ({
  name: {type: String, required: true, unique: true},
  email: {type: String, required: true},
  password: {type: String, required: true},
  address: {type: String, required: true},
  category: {type: String, required: true},
  contact: {type: String, required: true},
  profilePic: {type: String, required: false},
  description: {type: String, required: false},
  galleryUrls: [String],
  coords: {type: {
      type: String,
      enum: ['Point'],
      required: true
    },
    coordinates: {
      type: [Number],
      required: true
    }
  },
  requests: [String]
});

module.exports = mongoose.model('Organization', organizationSchema);
