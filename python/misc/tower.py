def towers_of_hanoi(n, start, end):
    assert 0 < start <= 3 and 0 < end <= 3 and start != end, "Bad start/end"
    offset = 6-start-end
    def move_disk(start, end):
        print('Move the top disk from rod', start, 'to rod', end)
    def check(n, start, end, offset):
        if n == 1:
            return move_disk(start, end)
        check(n, start, offset, end)
        move_disk(n, start, end)
        check(n, offset, end)
    return check(n, start, end, offset)

    


