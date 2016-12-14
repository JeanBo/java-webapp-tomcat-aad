require 'spec_helper'
describe 'testdemo1' do

  context 'with default values for all parameters' do
    it { should contain_class('testdemo1') }
  end
end
