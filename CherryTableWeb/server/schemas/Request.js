var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var requestSchema = new Schema ({
  title: {type: String, required: true},
  description: String,
  organization: {type: String, required: true},
  target: {type: Number, required: true},
  fulfilled: Number,
  donations: [String]
});

module.exports = mongoose.model('Request', requestSchema);
