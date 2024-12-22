local current = redis.call('get',KEYS[1])
if not current then
    return redis.call('setex',KEYS[1],60,1)
else
    current = tonumber(current)
    if current >= tonumber(ARGV[1]) then
        return true
    else
        return redis.call('incr',KEYS[1])
    end
end