FROM clamav/clamav:0.105.0
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
